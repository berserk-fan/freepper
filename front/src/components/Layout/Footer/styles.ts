import { makeStyles } from "@material-ui/core/styles";

export const useStyles = makeStyles(({ palette, typography, spacing }) => ({
  top: {
    backgroundSize: "cover",
    overflow: "hidden",
  },
  middle: {
    backgroundColor: palette.type === "dark" ? "#192D36" : palette.action.hover,
  },
  bottom: {
    backgroundColor:
      palette.type === "dark" ? "#0F2128" : palette.action.selected,
  },
  newsletterText: {
    color: "#fff",
    TypographySize: "0.875rem",
    textTransform: "uppercase",
  },
  form: {
    margin: 0,
    minWidth: 343,
    TypographySize: "0.875rem",
  },
  legalLink: {},
  divider: {
    height: 2,
    margin: "-1px 0",
  },
  overlay: {
    position: "absolute",
    top: 0,
    left: 0,
    bottom: 0,
    right: 0,
    filter: "grayscale(80%)",
    "& img": {
      width: "100%",
      height: "100%",
      objectFit: "cover",
    },
  },
  info: {
    ...typography.caption,
    position: "static",
    color: palette.text.hint,
    marginTop: 8,
    fontFamily: "Monospace",
  },
  title: {
    display: "flex",
    alignItems: "center",
    textTransform: "uppercase",
    marginBottom: "0.75em",
    letterSpacing: "2px",
    color: palette.text.primary,
    lineHeight: 1.5,
  },
  item: {
    display: "flex",
    alignItems: "center",
    fontSize: "1rem",
    color: palette.text.secondary,
    lineHeight: 1.8,
    marginBottom: "0.375em",
    cursor: "pointer",
    "&:hover, &:focus": {
      color: palette.text.primary,
    },
  },
  anchor: {
    display: "inline-flex",
    alignItems: "center",
    cursor: "pointer",
    fontSize: 24,
    padding: 12,
    borderColor: palette.divider,
    color: palette.text.secondary,
    position: "relative",
    "&:after": {
      content: '""',
      display: "block",
      position: "absolute",
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      zIndex: 0,
      backgroundColor:
        palette.type === "dark" ? palette.action.focus : palette.action.hover,
      borderRadius: 40,
      transition: "0.3s cubic-bezier(.47,1.64,.41,.8)",
      transform: "scale(0)",
    },
    "&:not(:first-of-type)": {
      marginLeft: "0.5rem",
    },
    "&:hover, &:focus": {
      color: palette.type === "dark" ? "#fff" : palette.primary.main,
      "&:after": {
        transform: "scale(1)",
      },
    },
  },
  icon: {
    position: "relative",
    zIndex: 1,
  },
  menu: {
    display: "flex",
    justifyContent: "center",
  },
  menuItem: {
    flexShrink: 0,
    display: "flex",
    alignItems: "center",
    borderRadius: 4,
    padding: spacing(1, 2),
    cursor: "pointer",
    textDecoration: "none",
    transition: "0.2s ease-out",
    textTransform: "uppercase",
    TypographyWeight: "bold",
    TypographySize: "0.75rem",
    justifyContent: "center",
    color: palette.text.hint,
    letterSpacing: "0.5px",
    "&:hover": {
      color: palette.type === "dark" ? "#fff" : palette.text.primary,
    },
  },
  menuItemActive: {
    "&$menuItem": {
      color: palette.type === "dark" ? "#fff" : palette.text.primary,
    },
  },
}));
