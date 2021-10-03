var { merge } = require('webpack-merge');

var globalConfig = require('./common.webpack.config');
var generatedConfig = require('./scalajs.webpack.config');

module.exports = merge(generatedConfig, globalConfig);