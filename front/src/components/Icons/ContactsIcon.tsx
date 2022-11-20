import SvgIcon from "@mui/material/SvgIcon";
import React, { memo } from "react";
import Contacts from "./svg/contacts.svg";

function ContactsIcon(props) {
  return (
    <SvgIcon fontSize="large" viewBox="0 0 512 512" {...props}>
      <Contacts />
    </SvgIcon>
  );
}

export default memo(ContactsIcon);
