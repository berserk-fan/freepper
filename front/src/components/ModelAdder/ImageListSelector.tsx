import { Form } from "react-final-form";
import React from "react";
import { ImageList } from "apis/image_list.pb";
import ImageListInput from "./ImageListInput";

export default function ImageListSelector({
  imageLists,
  onSelect,
}: {
  imageLists: ImageList[];
  onSelect: (list: ImageList) => void;
}) {
  return (
    <Form
      onSubmit={(vals) =>
        onSelect(imageLists.find((x) => x.name === vals.name) || null)
      }
      render={({ handleSubmit }) => (
        <ImageListInput
          label="Select an images"
          name="name"
          required
          color="secondary"
          imageLists={imageLists}
          onSelect={handleSubmit}
        />
      )}
    />
  );
}
