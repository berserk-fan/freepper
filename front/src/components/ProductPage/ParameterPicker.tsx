import Typography from "@mui/material/Typography";
import React, { MouseEventHandler } from "react";
import { Parameter, ParameterList } from "apis/parameter.pb";
import Box from "@mui/material/Box";
import makeStyles from "@mui/styles/makeStyles";
import Chip from "@mui/material/Chip";
import Avatar from "@mui/material/Avatar";
import { MyAvatar } from "../Commons/MyAvatar";

const useStyles = makeStyles({
  fabricNode: {
    margin: "2px",
  },
});

function MyChipNoMemo({
  parameter,
  selected,
  onClick,
}: {
  parameter: Parameter;
  selected: boolean;
  onClick: MouseEventHandler;
}) {
  const classes = useStyles();
  return (
    <Chip
      className={classes.fabricNode}
      avatar={
        parameter.image ? (
          <Avatar>
            <MyAvatar image={parameter.image} />
          </Avatar>
        ) : undefined
      }
      clickable
      color={selected ? "secondary" : "default"}
      variant="outlined"
      label={parameter.displayName}
      onClick={onClick}
    />
  );
}

const MyChip = React.memo(
  MyChipNoMemo,
  (prev, cur) =>
    prev.parameter.uid === cur.parameter.uid && prev.selected === cur.selected,
);

export default function ParameterPicker({
  parameterList,
  selectedParameterId,
  onChange,
}: {
  parameterList: ParameterList;
  selectedParameterId: string;
  onChange: (newParamId: string) => void;
}) {
  const indexed = React.useMemo(
    () => Object.fromEntries(parameterList.parameters.map((p) => [p.uid, p])),
    [parameterList],
  );

  const selectedParameter = indexed[selectedParameterId];

  return (
    <div>
      <Typography gutterBottom variant="subtitle2" component="h3">
        {parameterList.displayName} - {selectedParameter.displayName}
      </Typography>
      <Box className="flex overflow-x-auto">
        {parameterList.parameters.map((item) => (
          <MyChip
            key={item.uid}
            parameter={item}
            selected={selectedParameterId === item.uid}
            onClick={() => onChange(item.uid)}
          />
        ))}
      </Box>
    </div>
  );
}
