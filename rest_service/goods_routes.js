const Firestore = require('@google-cloud/firestore');

module.exports = function(app, db) {
  app.get('/goods/all', (req, res) => {
    let allGoods = [];
    db.collection('goods').get().then(snapshot => {
     snapshot.forEach(doc => {
      obj = doc.data();
      obj.date = doc.data().date._seconds;
      allGoods.push(obj);
    });
    res.send(allGoods);
  })
  .catch(err => {
    console.log(err);
    res.send({'error':'An error has occurred'});
  });
});
    app.get('/goods/:id', (req, res) => {
    let goodsRef = db.collection('goods').doc(req.params.id);
    let getDoc = goodsRef.get()
      .then(doc => {
        if (!doc.exists) {
          console.log('No such document!');
          res.send({'error':'An error has occurred'});
        } else {
          obj = doc.data();
          obj.date = doc.data().date.toDate();
          console.log(obj);
          res.send(obj);
        }
      })
      .catch(err => {
         res.send({'error':'An error has occurred'});
      });
    });

    app.post('/goods', (req, res) => {
    req.body.price = Number(req.body.price);
    let date = Date.parse(req.body.date);
    if(!date)
        req.body.date = Firestore.Timestamp.now();
    else
        req.body.date = Firestore.Timestamp.fromMillis(date);
    req.body.img = "img/default.png"
    let setDoc = db.collection('goods').add(req.body);
    res.send(setDoc);
  });
};

