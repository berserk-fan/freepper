const withBundleAnalyzer = require("@next/bundle-analyzer")({
  enabled: process.env.ANALYZE === "true",
});

const withMDX = require("@next/mdx")({
  extension: /\.mdx?$/,
});

module.exports = withBundleAnalyzer(
  withMDX({
    pageExtensions: ["js", "jsx", "ts", "tsx", "mdx"],
    images: {
      deviceSizes: [384, 500, 640, 750, 828, 1080, 1200, 1920, 2048, 3840],
      imageSizes: [16, 32, 48, 64, 96, 128, 256],
    },
    webpack(config) {
      config.module.rules.push({
        test: /\.svg$/,
        issuer: {
          test: /\.(js|ts)x?$/,
        },
        use: ["@svgr/webpack"],
      });

      return config;
    },
  })
);
