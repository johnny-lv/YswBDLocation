var exec = require('cordova/exec');

var ysw_baidu_location = {
  getCurrentPosition: function(successFn, failureFn) {
    exec(successFn, failureFn, 'YswBDLocation', 'getCurrentPosition', []);
  }
};

module.exports = ysw_baidu_location