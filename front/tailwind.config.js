module.exports = {
  theme: {},
  variants: {
    borderOpacity: ["hover"],
    borderWidth: ["checked"],
    borderColor: ["hover", "checked"],
    display: ["last"],
  },
  plugins: [],
  purge: {
    content: [
      "./src/**/*.html",
      "./src/**/*.ts",
      "./src/**/*.jsx",
      "./src/**/*.tsx",
      "./src/**/*.js",
    ],
  },
};
