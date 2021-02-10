import React from "react";
import { SvgIcon } from "@material-ui/core";
import Contacts from "./svg/contacts.svg";

export default function ContactsIcon(props) {
  return (
    <SvgIcon fontSize={"large"} viewBox={"0 0 512 512"} {...props}>
      {<Contacts />}
    </SvgIcon>
  );
}
