// Import Webpack npm module
const HtmlWebpackPlugin = require('html-webpack-plugin')
const webpack = require('webpack')
const path = require('path')

module.exports = {
  // Which file is the entry point to the application
  entry: './src/index.js',
  // Which file types are in our project, and where they are located
  resolve: {
    extensions: ['.js', '.jsx']
  },
  // Where to output the final bundled code to
  output: {
    path: path.resolve(__dirname, 'target/classes/public/'),
    filename: 'bundle.min.js',
    libraryTarget: 'umd'
  },
  devtool: '#source-map',
  module: {

    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: {
          loader: 'babel-loader'
        }
      },
      { test: /\.css$/, use: [
          'style-loader',
          'css-loader'
        ] },
    ],
  },
  plugins: [
    new webpack.DefinePlugin({
      "process.env": {
        NODE_ENV: JSON.stringify("production")
      }
    }),
    //new webpack.optimize.UglifyJsPlugin({ sourceMap: true}),
    new HtmlWebpackPlugin({
      filename: 'index.html',
      template: 'src/index.html'
    })
  ]
};
