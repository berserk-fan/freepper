import Typography from "@material-ui/core/Typography/Typography";
import React from "react";
import { Parameter, ParameterList } from "apis/parameter.pb";
import Box from "@material-ui/core/Box/Box";
import makeStyles from "@material-ui/core/styles/makeStyles";
import Chip from "@material-ui/core/Chip/Chip";
import Avatar from "@material-ui/core/Avatar/Avatar";
import Image from "next/image";
import { Image as ApiImage } from "apis/image_list.pb";

function Icon({ image }: { image: ApiImage }) {
  return <Image width={24} height={24} src={image.src} alt={image.alt} />;
}

const useStyles = makeStyles({
  fabricNode: {
    margin: "2px",
  },
});

function MyChipNoMemo({
  parameter,
  selected,
}: {
  parameter: Parameter;
  selected: boolean;
}) {
  const classes = useStyles();
  return (
    <Chip
      className={classes.fabricNode}
      avatar={
        <Avatar>
          <Icon image={parameter.image} />
        </Avatar>
      }
      clickable
      color={selected ? "secondary" : "default"}
      variant="outlined"
      label={parameter.displayName}
    />
  );
}

const MyChip = React.memo(
  MyChipNoMemo,
  (prev, cur) =>
    prev.parameter.id === cur.parameter.id && prev.selected === cur.selected,
);

export default function ParameterPicker({
  selectedParameterId,
  parameterList,
}: {
  selectedParameterId: string;
  parameterList: ParameterList;
}) {
  const selectedParameter = parameterList.parameters[selectedParameterId];
  return (
    <div>
      <Typography gutterBottom variant="subtitle2" component="h3">
        ${parameterList.displayName} - {selectedParameter.displayName}
      </Typography>
      <Box className="flex overflow-x-auto">
        {Object.values(parameterList.parameters).map((item) => (
          <MyChip
            key={item.id}
            parameter={selectedParameter}
            selected={selectedParameterId === item.id}
          />
        ))}
      </Box>
    </div>
  );
}
