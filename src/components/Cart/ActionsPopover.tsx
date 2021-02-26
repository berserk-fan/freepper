import PopupStateComponent, {
  bindPopover,
  bindTrigger,
} from "material-ui-popup-state";
import { Button, IconButton, Popover, Typography } from "@material-ui/core";
import MoreVertIcon from "@material-ui/icons/MoreVert";
import DeleteIcon from "@material-ui/icons/Delete";
import CloseIcon from "@material-ui/icons/Close";
import React from "react";

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
