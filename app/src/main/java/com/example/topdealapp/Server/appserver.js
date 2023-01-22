const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const firebase = require('firebase-admin');


// Parse request bodies as JSON
app.use(bodyParser.json());

// Fetch the service account key JSON file contents
// Replace this with the path to your service account key
const serviceAccount = require('./firebase.json');

// Initialize the app with a service account, granting admin privileges
firebase.initializeApp({
    credential: firebase.credential.cert(serviceAccount),
    databaseURL: 'https://topdeal-app-default-rtdb.firebaseio.com'
});

// As an admin, the app has access to read and write all data, regardless of Security Rules
const db = firebase.database();


//app.get('/', (req, res) => {
//    // Get a list of all users in the "users" collection
//    let phone = req.query.phone;
//    //let password = req.query.password;
//
//    db.ref('Users').on('child_added', (snapshot) => {
//    if(phone === snapshot.key){
//    res.send("Yes");
//    }
//    else{
//    res.send("No");
//    }
//
//    });
//    });

//    db.collection('Users').get()
//        .then((snapshot) => {
//
//
//            let valid = false;
//            for (let user of users) {
//                    if (email === user["email"]){
//                        if (password === user["password"]){
//                            res.send("correct!");
//                            valid = true;
//                            break;
//                        } else res.send("incorrect password!");
//                        return;
//                    }}
//            if (!valid){res.send("no mail exist!");}
//        })
//        .catch((err) => {
//            // An error occurred
//            res.status(500).send(err);
//        });
//});

app.get('/create/', (req,res) =>{
let name = req.query.name;
let phone = req.query.phone;
let password = req.query.password;
if(name === undefined  || phone === undefined  || password === undefined ){
return res.status(400).send("Error. You need to fill the name, phone and password");
}else if{
const usersRef = db.ref('Users');
usersRef.on('child_added', (snapshot) => {
if(phone === snapshot.key){
return res.status(400).send("Phone number is exists");
}
});
}
else{
//const usersRef = db.ref('Users');
//usersRef.child(phone).set({
//  name: name,
//  password: password,
//  phone: phone
//});
res.status(200).send("Done!");
}
});


app.get('/', (req, res) => {
    // Get a list of all users in the "users" collection
    let phone = req.query.phone;
    let password = req.query.password;
    db.ref('Users').getChildren()
        .then((snapshot) => {
            // Successfully retrieved data
            const users = [];
            snapshot.forEach((doc) => {
                users.push(doc.data());
            });
            let valid = false;
            for (let user of users) {
                    if (phone === user["phone"]){
                        if (password === user["password"]){
                            res.send("correct!");
                            valid = true;
                            break;
                        } else res.send("incorrect password!");
                        return;
                    }}
            if (!valid){res.send("no mail exist!");}
        })
        .catch((err) => {
            // An error occurred
            res.status(500).send(err);
        });
});

//app.get('/login/', (req,res) => {
//let phone = req.query.phone;
//let password = req.query.password;
//const usersRef = db.ref('Users');
//usersRef.on('child_added', (snapshot) => {
//if(phone === snapshot.key){
//if(password === snapshot.val().password){
//res.send('correct!');
//}
//}
//});
//
//});
//app.get('/login/', (req,res) =>{
//let phone = req.query.phone;
//let password = req.query.password;
//if(phone === undefined  || password === undefined){
//return res.status(400).send("Error. You need to fill the phone and password");
//}
//else{
//const usersRef = db.ref('Users');
//usersRef.on('child_added', (snapshot) =>{
//if(phone === snapshot.key){
//if(password === snapshot.val().password){
//return res.status(200).send("correct!");
//}
//else{
//return res.status(400).send("password wrong!");
//}
//return res.status(400).send("no phone exist!");
//}
//})
//}
//return res.status(500).send("Error!");
//})



//app.post('/create/', (req,res) => {
//    let id = req.query.id;
//    let name = req.query.name;
//    let phone = req.query.phone;
//    let password = req.query.password;
//    var ref = db.ref('Users');
//    ref.set({
//        id : {
//            'name': 'name'
//            'phone': 'phone'
//            'password': 'password'
//        }
//    });
//});
//app.get('/', (req, res) => {
//let phone = req.query.phone;
//var ref = db.ref('Users');
//ref.orderByKey().on('child_added', (snapshot) => {
//if(phone === snapshot.key){
//  //console.log(snapshot.key);
//  return res.send("Done");
//  }
//  else{
//  return res.send("Error")
//  }
//});
//});

app.listen(3000, () => {
    console.log('App listening on port 3000');
});






//app.get('/', (req, res) => {
//let sid = req.query.sid;
//db.ref('Sellers').on('child_added', (snapshot) => {
//if(sid === snapshot.key){
//console.log(snapshot.val());
//res.send("done");
////console.log(snapshot.key, snapshot.val().address, snapshot.val().email,snapshot.val().name,snapshot.val().sid,snapshot.val().phone);
////res.json(getStandardResponse(snapshot.key, snapshot.val().address, snapshot.val().email,snapshot.val().name,snapshot.val().sid,snapshot.val().phone));
//}
//else{
//res.send("error");
//}
//  //res.send(snapshot.val());
//}, (errorObject) => {
//  res.send('The read failed: ' + errorObject.name);
//});
//    let phone = req.query.phone;
//    db.ref('Users').orderByKey().on('child_added', (snapshot) => {
//    if(phone === snapshot.key){
//        //var result = JSON.stringify(snapshot.val());
//        return res.send("done");
//      }
//      else{
//        return res.send("no phone exist!");
//      }
//    });
//});
//
//app.listen(3000, () => {
//    console.log('App listening on port 3000');
//});