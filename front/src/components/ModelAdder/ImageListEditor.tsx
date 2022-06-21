import React from "react";
import { ImageList } from "apis/image_list.pb";
import Typography from "@material-ui/core/Typography/Typography";
import Box from "@material-ui/core/Box";
import { Form } from "react-final-form";
import { Autocomplete } from "mui-rff";
import { Radio as MuiRadio } from "@material-ui/core";
import Divider from "@material-ui/core/Divider";
import Button from "@material-ui/core/Button/Button";
import grpcClient from "../../commons/shopClient";
import SliderWithThumbs from "../SliderWithThumbs";
import { SIZES } from "../Shop/definitions";
import { useImageLists } from "../../commons/swrHooks";
import SwrFallback from "../Swr/SwrFallback";

export function ImageListController() {
  const imageLists1 = useImageLists();
  const indexed = React.useMemo(
    () => Object.fromEntries(imageLists1.data.map((x) => [x.name, x])),
    [imageLists1.data],
  );

  const autocompleteData = React.useMemo(
    () =>
      imageLists1.data.map((x) => ({ label: x.displayName, value: x.name })),
    [imageLists1.data],
  );

  const [activeImageSrc, setActiveImageSrc] = React.useState("");

  const [currentList, setCurrentList] = React.useState(
    ImageList.fromPartial({}),
  );

  React.useEffect(() => {
    if (!imageLists1.isLoading && imageLists1.data.length > 0) {
      setCurrentList(imageLists1.data[0]);
    }
  }, [imageLists1.data]);

  function deleteImage() {
    setCurrentList((prev) => ({
      ...prev,
      images: prev.images.filter((x) => x.src !== activeImageSrc),
    }));
  }

  async function submitNewImageList() {
    const imageList = await grpcClient().updateImageList({
      imageList: currentList,
      updateMask: ["*"],
    });
    setCurrentList(imageList);
    alert(`ImageList updated: ${JSON.stringify(imageList)}`);
    await imageLists1.mutate();
  }

  return (
    <SwrFallback
      name="ImageLists"
      swrData={imageLists1}
      main={() => (
        <Box maxWidth="500px">
          <Typography variant="subtitle1">Name: {currentList.name}</Typography>
          <Typography variant="h6">{currentList.displayName}</Typography>
          <Form
            onSubmit={(vals) => setCurrentList(indexed[vals.name])}
            initialValues={{ name: imageLists1.data[0].name }}
            render={({ handleSubmit }) => (
              <Autocomplete
                label="Select an images"
                name="name"
                required
                color="secondary"
                options={autocompleteData}
                getOptionValue={(option) => option.value}
                getOptionLabel={(option) => option.label}
                onSelect={handleSubmit}
                renderOption={(option, { selected }) => (
                  <>
                    <MuiRadio style={{ marginRight: 8 }} checked={selected} />
                    {option.label}
                  </>
                )}
              />
            )}
          />
          <Box marginY={2}>
            <Divider />
            <Button variant="outlined" onClick={submitNewImageList}>
              Submit Image List
            </Button>
            <Button variant="outlined" onClick={deleteImage}>
              Delete Image
            </Button>
          </Box>
          <Box maxHeight="500px" maxWidth="500px">
            <SliderWithThumbs
              images={currentList.images}
              thumbs={currentList.images}
              sizes={SIZES}
              onChange={setActiveImageSrc}
            />
          </Box>
        </Box>
      )}
    />
  );
}
