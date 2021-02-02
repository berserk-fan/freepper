import React from "react";
import {Box, Container, Link, List, ListItem, ListItemText, Typography} from "@material-ui/core";
import LayoutWithHeaderAndFooter from "../components/Layout/LayoutWithHeaderAndFooter";

/*
What information is collected
Where information is collected from
Why information is collected
How information is collected (including through cookies and other tracking technologies)
Who information is shared with or sold to
What rights users have over their data
 */

export default function PrivacyPolicy() {
    return (
        <LayoutWithHeaderAndFooter>
            <Container>
                <Box py={10} px={2}>
                    <Typography variant={"h3"} component={"h1"}>Политика конфеденциальности</Typography>
                    <List>
                        {["1. Мы собираем информацию необходимую для отправки заказа и связи включая имя, адрес, имейл, номер телефона.",
                            "2. Эта информация собирается во время отправки формы.",
                            "3. Информация отправляется на почту и храниться на имейл серверах.",
                            "4. Информация о клиенте передается почтовой службе.",
                            "5. Информация не передается никому другому и не продается.",
                            "6. Мы не используем куки.",
                            "7. Пользователи могут попросить удалить информацию о них после успешного сделанного заказа."]
                            .map(text => (<ListItem>
                                    <ListItemText>
                                        <Typography variant={"body2"}>{text}</Typography>
                                    </ListItemText>
                                </ListItem>)
                            )}
                    </List>
                    <Typography variant={"caption"}>Политика конфеденциальности имейл провайдера <a>https://www.zoho.com/privacy.html</a></Typography>
                </Box>
            </Container>
        </LayoutWithHeaderAndFooter>
    )
}
