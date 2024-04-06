var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function (req, res, next) {

  res.render('login', {
    title: 'Login'
  });
});
router.get('/register', function (req, res, next) {
  res.render('register', { title: 'Register' });
});
router.get('/login', function (req, res, next) {
  res.render('login', { title: 'Login' });
});
router.get('/home', function (req, res, next) {
  res.render('home', { title: 'home' });
});
router.get('/car-management', function (req, res, next) {
  res.render('car-management', { title: 'car management' });
});


module.exports = router;
