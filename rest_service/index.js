const goodsRoutes = require('./goods_routes');
module.exports = function(app, db) {
  goodsRoutes(app, db);
};