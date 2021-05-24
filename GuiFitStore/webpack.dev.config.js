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

  output: {
    path: path.resolve(__dirname),
    filename: 'bundle.js',
    libraryTarget: 'umd'
  },

  devServer: {
    historyApiFallback: true,
    contentBase: './',
    hot: true,
   // contentBase: path.resolve(__dirname) + '/src/',
    proxy: {
      '/api': 'http://localhost:8080',
      '/login': 'http://localhost:8080',
      '/logout': 'http://localhost:8080'
    },
    compress: true,
    port: 9000,
    host: 'localhost',
    open: true
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
    new HtmlWebpackPlugin({
      filename: 'index.html',
      template: 'src/index.html'
    }),
    new webpack.HotModuleReplacementPlugin()
  ]
}
