    import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import styleImport, {VantResolve} from 'vite-plugin-style-import';

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue(), styleImport({
        resolves: [VantResolve()],
    }),],
    // server: {
    //     proxy: {
    //         '/api': {
    //             target: '127.0.0.1:8080/api',
    //             changeOrigin: true,
    //             secure: false,
    //             rewrite: (path) => path.replace(/^\/api/, '')
    //         }
    //     }
    // }
})

