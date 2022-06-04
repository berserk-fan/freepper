import PopupStateComponent, {
  bindPopover,
  bindTrigger,
} from "material-ui-popup-state";
import MoreVertIcon from "@material-ui/icons/MoreVert";
import DeleteIcon from "@material-ui/icons/Delete";
import CloseIcon from "@material-ui/icons/Close";
import React from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import Popover from "@material-ui/core/Popover/Popover";
import Button from "@material-ui/core/Button/Button";
import Typography from "@material-ui/core/Typography/Typography";

export default function ActionsPopover({
  productId,
  deleteProduct,
}: {
  productId: string;
  deleteProduct: (id: string) => void;
}) {
  return (
    // @ts-ignore
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
