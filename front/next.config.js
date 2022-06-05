const path = require('path');
const withBundleAnalyzer = require("@next/bundle-analyzer")({
    enabled: process.env.ANALYZE === "true"
});

const withMDX = require("@next/mdx")({
    extension: /\.mdx?$/
});

module.exports = withBundleAnalyzer(
  withMDX({
    pageExtensions: ["js", "jsx", "ts", "tsx", "mdx"],
    images: {
      deviceSizes: [384, 500, 640, 750, 828, 1080, 1200, 1920, 2048, 3840],
      imageSizes: [16, 32, 48, 64, 96, 128, 256],
      loader: 'imgix',
      path: 'https://pomo.imgix.net/',
    },
    webpack(config) {
      config.module.rules.push({
        test: /\.svg$/,
        use: ["@svgr/webpack"]
      });
      config.resolve.alias['Public'] = path.join(__dirname, 'public')
      return config;
    }
  })
);

/**
 * SERIALIZATION FIXES
 * HACK HACK HACK HACK HACK HACK HACK HACK HACK HACK HACK HACK HACK HACK HACK HACK
 *
 * 1. Remove undefined values so Next.js doesn't complain during serialization. Verified as of v11.1.2.
 * https://github.com/vercel/next.js/discussions/11209
 *
 * 2. Remove .toObject that is present on gRPC responses.
 */
const removeUndefined = (obj) => {
    //delete .toObject method that is generated in ts-node
    delete obj.toObject

    let newObj = {};
    Object.keys(obj).forEach((key) => {
        if (obj[key] === Object(obj[key])) newObj[key] = removeUndefined(obj[key]);
        else if (obj[key] !== undefined) newObj[key] = obj[key];
    });
    return newObj;
};
const next = require('next/dist/lib/is-serializable-props');
const isSerializableProps = next.isSerializableProps;
next.isSerializableProps = (page, method, input) => isSerializableProps(page, method, removeUndefined(input));
