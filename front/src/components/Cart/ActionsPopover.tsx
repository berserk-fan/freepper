import PopupStateComponent, {
  bindPopover,
  bindTrigger,
} from "material-ui-popup-state";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import DeleteIcon from "@mui/icons-material/Delete";
import CloseIcon from "@mui/icons-material/Close";
import React from "react";
import IconButton from "@mui/material/IconButton";
import Popover from "@mui/material/Popover";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";

export default function ActionsPopover({
  productId,
  deleteProduct,
}: {
  productId: string;
  deleteProduct: (id: string) => void;
}) {
  return (
    <PopupStateComponent variant="popover" popupId="cart-action-popover">
      {(popupState) => (
        <div>
          <IconButton size="small" {...bindTrigger(popupState)}>
            <MoreVertIcon />
          </IconButton>
          <Popover
            {...bindPopover(popupState)}
            anchorOrigin={{
              vertical: "top",
              horizontal: "left",
            }}
            transformOrigin={{
              vertical: "top",
              horizontal: "right",
            }}
          >
            <div className="flex flex-col">
              <Button
                onClick={() => {
                  deleteProduct(productId);
                  popupState.close();
                }}
                fullWidth
                startIcon={<DeleteIcon />}
              >
                <Typography>Удалить из корзины</Typography>
              </Button>
              <Button
                onClick={popupState.close}
                fullWidth
                startIcon={<CloseIcon />}
              >
                <Typography>Закрыть</Typography>
              </Button>
            </div>
          </Popover>
        </div>
      )}
    </PopupStateComponent>
  );
}
