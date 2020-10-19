module.exports = {
  theme: {},
  variants: {
    borderOpacity: ['hover'],
    borderWidth: ['checked'],
    borderColor: ['hover', 'checked']
  },
  plugins: [],
  purge: {
    content: ["./src/**/*.html", "./src/**/*.re", "./src/**/*.bs.js"],
  },
};

