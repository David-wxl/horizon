/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // HORIZON Premium Color System
        'horizon': {
          'bg-start': '#1e1b4b',    // 深紫
          'bg-mid': '#1e3a8a',      // 深蓝
          'bg-end': '#0f172a',      // 接近黑
          'primary': '#ffffff',      // 主文字
          'secondary': '#cbd5e1',    // 次要文字（slate-300）
          'accent-purple': '#a855f7', // 强调紫
          'accent-cyan': '#06b6d4',   // 强调青
        },
      },
      fontFamily: {
        'sans': ['Inter', 'ui-sans-serif', 'system-ui', 'sans-serif'],
        'display': ['Inter Display', 'Inter', 'sans-serif'],
        'mono': ['JetBrains Mono', 'monospace'],
      },
      backdropBlur: {
        'glass': '40px',
      },
      boxShadow: {
        'glow': '0 0 60px rgba(168, 85, 247, 0.15)',
        'glow-lg': '0 0 80px rgba(168, 85, 247, 0.25)',
        'glow-cyan': '0 0 60px rgba(6, 182, 212, 0.15)',
      },
      animation: {
        'float': 'float 6s ease-in-out infinite',
        'glow': 'glow 2s ease-in-out infinite alternate',
      },
      keyframes: {
        float: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-20px)' },
        },
        glow: {
          '0%': { boxShadow: '0 0 20px rgba(168, 85, 247, 0.5)' },
          '100%': { boxShadow: '0 0 40px rgba(168, 85, 247, 0.8)' },
        },
      },
    },
  },
  plugins: [],
}
