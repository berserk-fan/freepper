import { createMuiTheme, responsiveFontSizes } from "@material-ui/core/styles";
import { red } from "@material-ui/core/colors";

// Create a theme instance.
const theme = createMuiTheme({
  spacing: 8,
  typography: {
    fontFamily: ["'Roboto', sans-serif", "'Merriweather', serif", "Monospace", "Montserrat"].join(","),
    h1: {
      fontSize: 96,
      fontFamily: "'Merriweather', serif",
      fontWeight: 900,
      fontStyle: "italic",
    },
    h2: {
      fontSize: 60,
      fontFamily: "'Roboto', sans-serif",
      fontWeight: 300,
    },
    h3: {
      fontSize: 48,
      fontFamily: "'Merriweather', serif",
      fontWeight: 900,
      fontStyle: "italic",
    },
    h4: {
      fontSize: 34,
      fontFamily: "'Montserrat', sans-serif",
      fontWeight: 400,
    },
    h5: {
      fontSize: 24,
      fontFamily: "'Roboto', sans-serif",
      fontWeight: 400,
    },
    h6: {
      fontSize: 20,
      fontFamily: "'Merriweather', serif",
      fontWeight: 700,
      fontStyle: "italic",
    },
    subtitle1: {
      fontSize: 16,
      fontFamily: "'Roboto', sans-serif",
      fontWeight: 500,
    },
    subtitle2: {
      fontSize: 14,
      fontFamily: "'Merriweather', serif",
      fontWeight: 400,
    },
    body1: {
      fontSize: 16,
      fontFamily: "'Merriweather', serif",
      fontWeight: 400,
    },
    body2: {
      fontSize: 16,
      fontFamily: "'Roboto', sans-serif",
      fontWeight: 400,
    },
    button: {
      fontSize: 14,
      fontFamily: "'Roboto', sans-serif",
      fontWeight: 700,
    },
    caption: {
      fontSize: 12,
      fontFamily: "'Merriweather', serif",
      fontWeight: 400,
      fontStyle: "italic",
    },
    overline: {
      fontSize: 10,
      fontFamily: "'Roboto', sans-serif",
      fontWeight: 700,
      textTransform: "capitalize",
    },
  },
  palette: {
    primary: {
      main: "#FFFFFF",
    },
    secondary: {
      main: "#81c3db",
    },
    error: {
      main: red.A400,
    },
  },
  breakpoints: {
    values: {
      xs: 0,
      sm: 600,
      md: 960,
      lg: 1280,
      xl: 1920,
    },
  },
  overrides: {
    MuiButton: {
      containedPrimary: {
        borderTop: 1,
        borderLeft: 1,
        borderColor: "#CCCCCC",
        borderStyle: "solid",
      },
    },
  },
});

export default responsiveFontSizes(theme);
