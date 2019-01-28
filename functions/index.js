let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/messages/{userId}/{messageId}').onWrite(event => {
	
	//get the userId of the person receiving the notification because we need to get their token
	const receiverId = event.params.userId;
	console.log("receiverId: ", receiverId);
	
	//get the user id of the person who sent the message
	const senderId = event.data.child('user_id').val();
	console.log("senderId: ", senderId);
	
	//get the message
	const message = event.data.child('message').val();
	console.log("message: ", message);
	
	//get the message id. We'll be sending this in the payload
	const messageId = event.params.messageId;
	console.log("messageId: ", messageId);
	
	//query the users node and get the name of the user who sent the message
	return admin.database().ref("/users/" + senderId).once('value').then(snap => {
		const senderName = snap.child("name").val();
		console.log("senderName: ", senderName);
		
		//get the token of the user receiving the message
		return admin.database().ref("/users/" + receiverId).once('value').then(snap => {
			const token = snap.child("messaging_token").val();
			console.log("token: ", token);
			
			//we have everything we need
			//Build the message payload and send the message
			console.log("Construction the notification message.");
			const payload = {
				data: {
					data_type: "direct_message",
					title: "New Message from " + senderName,
					message: message,
					message_id: messageId,
				}
			};
			
			return admin.messaging().sendToDevice(token, payload)
						.then(function(response) {
							console.log("Successfully sent message:", response);
							return true;
						  })
						  .catch(function(error) {
							console.log("Error sending message:", error);
						  });
		});
	});
});
