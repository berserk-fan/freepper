import React, { ReactNode } from "react";
import { withStyles, Theme } from "@material-ui/core/styles";
import Typography, { TypographyProps } from "@material-ui/core/Typography";
import Link from "@material-ui/core/Link";
import Table from "@material-ui/core/Table";
import Paper from "@material-ui/core/Paper";
import {
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  StyledComponentProps,
  ListItem,
  ListItemText,
  List,
  ListItemIcon,
  useTheme
} from "@material-ui/core";
import ReactMarkdown from "react-markdown";
import { ListContent, List as MdList } from "mdast";
import FiberManualRecordIcon from "@material-ui/icons/FiberManualRecord";

const styles = {
  header: {
    paddingTop: "1rem",
  },
  listIcon: {
    minWidth: 0,
  },
};

function MarkdownParagraph(props: { children: ReactNode }) {
  return <Typography>{props.children}</Typography>;
}

interface MdHeadingProps {
  level: number;
}

const MarkdownHeading = withStyles(styles)(
  ({
    classes,
    ...props
  }: StyledComponentProps & TypographyProps & MdHeadingProps) => {
    let variant: TypographyProps["variant"];
    switch (props.level) {
      case 1:
        variant = "h3";
        break;
      case 2:
        variant = "h4";
        break;
      case 3:
        variant = "h5";
        break;
      case 4:
        variant = "h6";
        break;
      case 5:
        variant = "subtitle1";
        break;
      case 6:
        variant = "subtitle2";
        break;
      default:
        variant = "h6";
        break;
    }
    return (
      <Typography className={classes?.header} gutterBottom variant={variant}>
        {props.children}
      </Typography>
    );
  }
);

const MarkdownListItem = withStyles(styles)((props: ListContent & any) => {
  const theme = useTheme();
  return (
    <ListItem
      style={{ paddingTop: 0, paddingBottom: 0 }}
      alignItems={"flex-start"}
    >
      <ListItemIcon style={{ minWidth: 18 }}>
        <FiberManualRecordIcon
          style={{ fontSize: "12px", color: theme.palette.grey["800"] }}
        />
      </ListItemIcon>
      <ListItemText style={{ marginTop: 2 }}>
        <Typography component="span" variant={"body2"}>
          {props.children}
        </Typography>
      </ListItemText>
    </ListItem>
  );
});

const MarkdownList = (props: MdList) => {
  return (
    <List dense disablePadding>
      {props.children}
    </List>
  );
};

function MarkdownTable(props: { children: ReactNode }) {
  return (
    <Paper>
      <Table size="small" aria-label="a dense table">
        {props.children}
      </Table>
    </Paper>
  );
}

function MarkdownTableCell(props: { children: ReactNode }) {
  return (
    <TableCell>
      <Typography>{props.children}</Typography>
    </TableCell>
  );
}

function MarkdownTableRow(props: { children: ReactNode }) {
  return <TableRow>{props.children}</TableRow>;
}

function MarkdownTableBody(props: { children: ReactNode }) {
  return <TableBody>{props.children}</TableBody>;
}

function MarkdownTableHead(props: { children: ReactNode }) {
  return <TableHead>{props.children}</TableHead>;
}

const renderers = {
  heading: MarkdownHeading,
  paragraph: MarkdownParagraph,
  link: Link,
  list: MarkdownList,
  listItem: MarkdownListItem,
  table: MarkdownTable,
  tableHead: MarkdownTableHead,
  tableBody: MarkdownTableBody,
  tableRow: MarkdownTableRow,
  tableCell: MarkdownTableCell,
};

export default function Markdown(props: ReactMarkdown.ReactMarkdownProps) {
  return <ReactMarkdown renderers={props.renderers || renderers} {...props} />;
}
