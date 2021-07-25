import makeStyles from "@material-ui/core/styles/makeStyles";

export const useStyles = makeStyles((theme) => ({
  thumb: {
    border: "3px solid transparent",
    borderRadius: "3px",
  },
  thumbActive: {
    border: `3px solid ${theme.palette.secondary.main}`,
    borderRadius: "3px",
  },
  thumbSlide: {
    display: "flex",
    marginTop: theme.spacing(0.5),
    marginBottom: theme.spacing(1),
  },
  forwardButton: {
    position: "absolute",
    right: 3,
    top: "50%",
    transform: "translate(0,-50%)",
    zIndex: 10,
    background: "rgba(255, 255, 255, 0.3);",
    borderRadius: "50%",
    width: 30,
    height: 30,
    boxShadow: "0 2px 4px 0 rgba(0,0,0,0.5)",
    "&:hover": {
      background: "rgba(255, 255, 255, 0.7);",
    },
  },
  backButton: {
    position: "absolute",
    left: 3,
    top: "50%",
    transform: "translate(0,-50%)",
    zIndex: 10,
    background: "rgba(255, 255, 255, 0.3);",
    borderRadius: "50%",
    width: 30,
    height: 30,
    boxShadow: "0 2px 4px 0 rgba(0,0,0,0.5)",
    outline: "none",
    "&:hover": {
      background: "rgba(255, 255, 255, 0.7);",
    },
  },
  icon: {
    width: 20,
    height: 20,
    color: "kr_black",
  },
  bigButton: {
    width: 40,
    height: 40,
  },
  smallButton: {
    width: 20,
    height: 20,
    background: "rgba(255, 255, 255, 0.9);",
  },
  smallIcon: {
    width: 15,
    height: 15,
  },
}));
