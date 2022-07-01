import AvatarGroup from "@mui/material/AvatarGroup";
import { Autocomplete } from "mui-rff";
import React from "react";
import { ImageList } from "apis/image_list.pb";
import { AutocompleteProps } from "mui-rff/src/Autocomplete";
import { MyAvatar } from "../Commons/MyAvatar";

export default function ImageListInput(
  props: { imageLists: ImageList[] } & Omit<
    AutocompleteProps<ImageList, undefined, undefined, undefined>,
    "options" | "renderOption" | "getOptionValue"
  >,
) {
  return (
    <Autocomplete<ImageList, undefined, undefined, undefined>
      options={props.imageLists}
      getOptionValue={(option) => option.name}
      getOptionLabel={(option) => (option as ImageList).displayName}
      renderOption={(props1, option) => (
        <li {...props1}>
          <AvatarGroup max={3}>
            {option.images.map((x) => (
              <MyAvatar variant="big" key={x.name} image={x} />
            ))}
          </AvatarGroup>
          {option.displayName}
        </li>
      )}
      {...props}
    />
  );
}
