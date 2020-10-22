module.exports = {
  theme: {},
  variants: {
    borderOpacity: ['hover'],
    borderWidth: ['checked'],
    borderColor: ['hover', 'checked']
  },
  plugins: [],
  purge: {
    content: ["./src/**/*.html", "./src/**/*.ts", "./src/**/*.jsx", "./src/**/*.tsx", "./src/**/*.js"],
  },
};

