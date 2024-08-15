const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const webpack = require('webpack');
const devMode = process.env.NODE_ENV !== 'production';

const plugins = [];
if (!devMode) {
    // enable in production only
    plugins.push(new MiniCssExtractPlugin({
    filename: 'css/shoperal.css'
    }));
}

if (devMode) {
    // enable in development
    plugins.push(new webpack.SourceMapDevToolPlugin({
        filename: 'js/shoperal.js.map',
    }));
}

module.exports = {
  plugins,
  entry: './src/main.ts',
  devtool: false,
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'js/shoperal.js',
  },
  module: {
    rules: [
        { 
            test: /\.s[ac]ss$/i,
            use: [
                // Creates `style` nodes from JS strings
                devMode ? 'style-loader' : MiniCssExtractPlugin.loader,
                // Translates CSS into CommonJS
                "css-loader",
                'postcss-loader',
                // Compiles Sass to CSS
                "sass-loader",] 
        },
        {
            test: /\.tsx?$/,
            use: 'ts-loader',
            exclude: /node_modules/,
        },
    ],
  },
  resolve: {
    extensions: ['.tsx', '.ts', '.js'],
  },
  optimization: {
    splitChunks: {
        cacheGroups: {
            styles: {
                name: 'styles',
                type: 'css/mini-extract',
                // For webpack@4
                // test: /\.css$/,
                chunks: 'all',
                enforce: true,
              },
        },
    },
  },
};