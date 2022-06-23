import { Theme, responsiveFontSizes } from "@mui/material/styles";
import { red } from "@mui/material/colors";
import { createTheme } from "@mui/material";

declare module "@mui/material/styles" {
  interface TypeText {
    hint: TypeText["primary"];
  }
}

declare module "@mui/styles/defaultTheme" {
  interface DefaultTheme extends Theme {}
}

const theme = createTheme({
  spacing: 8,
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
    text: {
      hint: "rgba(0, 0, 0, 0.38)",
    },
  },
  typography: {
    fontFamily: ["Roboto, sans-serif", "Monospace"].join(","),
    h1: {
      fontSize: 96,
      fontFamily: "Roboto, sans-serif",
      fontWeight: 500,
    },
    h2: {
      fontSize: 60,
      fontFamily: "Roboto, sans-serif",
      fontWeight: 700,
    },
    h3: {
      fontSize: 48,
      fontFamily: "Roboto, sans-serif",
      fontWeight: 700,
    },
    h4: {
      fontSize: 34,
      fontFamily: "Roboto, sans-serif",
      fontWeight: 400,
    },
    h5: {
      fontSize: 24,
      fontFamily: "Roboto, sans-serif",
      fontWeight: 400,
    },
    h6: {
      fontSize: 20,
      fontFamily: "Roboto, sans-serif",
      fontWeight: 700,
      fontStyle: "italic",
    },
    subtitle1: {
      fontSize: 16,
      fontFamily: "Roboto, sans-serif",
      fontWeight: 500,
    },
    subtitle2: {
      fontSize: 14,
      fontFamily: "Roboto, sans-serif",
      fontWeight: 400,
    },
    body1: {
      fontSize: 16,
      fontFamily: "Roboto, sans-serif",
      fontWeight: 400,
    },
    body2: {
      fontSize: 16,
      fontFamily: "Roboto, sans-serif",
      fontWeight: 400,
    },
    button: {
      fontSize: 14,
      fontFamily: "Roboto, sans-serif",
      fontWeight: 700,
    },
    caption: {
      fontSize: 12,
      fontFamily: "Roboto, sans-serif",
      fontWeight: 400,
      fontStyle: "italic",
    },
    overline: {
      fontSize: 10,
      fontFamily: "Roboto, sans-serif",
      fontWeight: 700,
      textTransform: "capitalize",
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
  components: {
    MuiButton: {
      styleOverrides: {
        containedPrimary: {
          borderTop: 1,
          borderLeft: 1,
          borderColor: "#CCCCCC",
          borderStyle: "solid",
        },
      },
    },
  },
});

export default responsiveFontSizes(theme);
