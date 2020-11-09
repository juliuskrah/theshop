module.exports = {
    filenameHashing: false,
    configureWebpack: {
        optimization: {
            splitChunks: false,
        },
        resolve: {
            alias: {
                'vue$': 'vue/dist/vue.esm.js'
            }
        }
    },
    pluginOptions: {
        apollo: {
            lintGQL: true
        }
    }
}
