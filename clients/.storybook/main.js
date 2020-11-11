const path = require("path");

module.exports = {
  stories: ["../src/**/*.stories.mdx", "../src/**/*.stories.@(js|jsx|ts|tsx)"],
  addons: [
    "@storybook/addon-links",
    "@storybook/addon-essentials",
    "@storybook/preset-scss",
  ],
  webpackFinal: async (config, { configType }) => {
    config.module.rules.push({
      test: /\.scss$/,
      loaders: "postcss-loader",
      options: {
        sourceMap: true,
        config: {
          path: "./.storybook/",
        },
      },
      include: path.resolve(__dirname, "../"),
    });
    
    return config;
  },
};
