import React, { memo } from "react";
import Checkbox from "@mui/material/Checkbox";
import Divider from "@mui/material/Divider";
import Paper from "@mui/material/Paper";
import MuiTable from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Typography from "@mui/material/Typography";
import FiberManualRecordIcon from "@mui/icons-material/FiberManualRecord";
import List from "@mui/material/List";
import useTheme from "@mui/material/styles/useTheme";
import ListItem from "@mui/material/ListItem";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";

export const components = {
  p: Typography,
  h1: (() => {
    const H1 = (props) => <Typography {...props} component="h1" variant="h1" />;
    return memo(H1);
  })(),
  h2: (() => {
    const H2 = (props) => <Typography {...props} component="h2" variant="h2" />;
    return memo(H2);
  })(),
  h3: (() => {
    const H3 = (props) => <Typography {...props} component="h3" variant="h3" />;
    return memo(H3);
  })(),
  h4: (() => {
    const H4 = (props) => <Typography {...props} component="h4" variant="h4" />;
    return memo(H4);
  })(),
  h5: (() => {
    const H5 = (props) => <Typography {...props} component="h5" variant="h5" />;
    return memo(H5);
  })(),
  h6: (() => {
    const H6 = (props) => <Typography {...props} component="h6" variant="h6" />;
    return memo(H6);
  })(),
  blockquote: (() => {
    const Blockquote = (props) => (
      <Paper style={{ borderLeft: "4px solid grey", padding: 8 }} {...props} />
    );
    return memo(Blockquote);
  })(),
  ul: (() => {
    const Ul = (props) => <List dense {...props} component="ul" />;
    return memo(Ul);
  })(),
  ol: (() => {
    const Ol = (props) => <List dense {...props} component="ol" />;
    return memo(Ol);
  })(),
  li: (() => {
    const Li = (props) => {
      const theme = useTheme();
      return (
        <ListItem
          style={{ paddingTop: 0, paddingBottom: 0 }}
          alignItems="flex-start"
        >
          <ListItemIcon style={{ minWidth: 18 }}>
            <FiberManualRecordIcon
              style={{ fontSize: "12px", color: theme.palette.secondary.dark }}
            />
          </ListItemIcon>
          <ListItemText style={{ marginTop: 2 }}>
            <Typography component="span" variant="body2">
              {props.children}
            </Typography>
          </ListItemText>
        </ListItem>
      );
    };
    return memo(Li);
  })(),
  table: (() => {
    const Table = (props) => <MuiTable {...props} />;
    return memo(Table);
  })(),
  tr: (() => {
    const Tr = (props) => <TableRow {...props} />;
    return memo(Tr);
  })(),
  td: (() => {
    const Td = ({ align, ...props }) => (
      <TableCell align={align || undefined} {...props} />
    );
    return memo(Td);
  })(),
  tbody: (() => {
    const TBody = (props) => <TableBody {...props} />;
    return memo(TBody);
  })(),
  th: (() => {
    const Th = ({ align, ...props }) => (
      <TableCell align={align || undefined} {...props} />
    );
    return memo(Th);
  })(),
  thead: (() => {
    const THead = (props) => <TableHead {...props} />;
    return memo(THead);
  })(),
  hr: Divider,
  input: (() => {
    const Input = (props) => {
      const { type } = props;
      if (type === "checkbox") {
        return <Checkbox {...props} disabled={false} readOnly />;
      }
      return <input {...props} />;
    };
    return memo(Input);
  })(),
};
