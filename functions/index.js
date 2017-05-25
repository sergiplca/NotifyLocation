const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

//Send a notification to all users when a message is changed
exports.sendNotifications = functions.database.ref('/comments/{message-id}/comment').onWrite(event => {
    const snapshot = event.data;

    //if (snapshot.previous.val()) {
    //    return;
    //}

    const text = snapshot.val();
    const payload = {
        notification: {
            title: `${snapshot.val().name} posted ${text ? 'a message' : 'an image'}`,
            body: text,
            icon: snapshot.val().photoUrl || '/images/profile_placeholder.png',
            click_action: `https://${functions.config().firebase.authDomain}`
        }
    };

    return admin.database().ref('tokensdevices').once('value').then(allTokens => {
        if (allTokens.val()) {
            //Listing all tokens
            const tokens = Object.keys(allTokens.val());

            //Send notifications to all tokens
            return admin.messaging().sendToDevice(tokens, payload).then(response => {
                //For each message check if there was an error
                const tokensToRemove = [];
                response.results.forEach((result, index) => {
                    const error = result.error;
                    if (error) {
                        console.error('Failure sending notification to', tokens[index], error);
                        //Cleanup the tokens who are not registered anymore
                        if (error.code === 'messaging/invalid-registration-token' ||
                            error.code === 'messaging/registration-token-not-registered') {
                            tokensToRemove.push(allTokens.ref.child(tokens[index]).remove());
                        }
                    }
                });
                return Promise.all(tokensToRemove);
            });
        }
    });
});