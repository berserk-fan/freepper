import { makeStyles } from "@material-ui/styles";
import { Theme } from "@material-ui/core";

export const useStyles = makeStyles((theme: Theme) => ({
  thumb: {
    border: "3px solid transparent",
    borderRadius: "3px",
  },
  thumbActive: {
    border: `3px solid ${theme.palette.secondary.main}`,
    borderRadius: "3px",
  },
  navigationContainer: {
    bottom: 0,
    padding: "10px",
  },
  thumbSlide: {
    display: "flex",
    marginTop: theme.spacing(0.5),
    marginBottom: theme.spacing(1),
  },
  forwardButton: {
    position: "absolute",
    right: 0,
    top: "50%",
    transform: "translate(50%,-50%)",
    zIndex: 10,
    background: "#fff",
    outline: "none",
    borderRadius: "50%",
    boxShadow: "0 2px 4px 0 rgba(0,0,0,0.1)",
    "&:hover": {
      background: "#fff",
    },
  },
  backButton: {
    position: "absolute",
    left: 0,
    top: "50%",
    transform: "translate(-50%,-50%) rotate(180deg)",
    zIndex: 10,
    background: "#fff",
    borderRadius: "50%",
    outline: "none",
    boxShadow: "0 -2px 4px 0 rgba(0,0,0,0.1)",
    "&:hover": {
      background: "#fff",
    },
  },
  bigButton: {
    width: 40,
    height: 40,
  },
  smallButton: {
    width: 20,
    height: 20,
  },
  icon: {
    width: 24,
    height: 24,
    color: theme.palette.secondary.main,
  },
  smallIcon: {
    width: 15,
    height: 15,
    color: theme.palette.secondary.main,
  },
}));
