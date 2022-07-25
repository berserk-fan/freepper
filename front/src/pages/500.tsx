import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import LayoutWithHeaderAndFooter from "../components/Layout/LayoutWithHeaderAndFooter";
import ConactUs from "../components/ContactUs/ContactUs";

export default function Custom404() {
  return (
    <>
      <LayoutWithHeaderAndFooter disableBreadcrumbs>
        <Container maxWidth="sm">
          <Box
            className="center"
            display="flex"
            flexDirection="column"
            justifyContent="center"
            alignItems="center"
          >
            <Typography variant="h3" component="h1">
              Упс. Что-то пошло нет так 😱. Сообщите о проблеме написав нам 😎
              <ConactUs />
            </Typography>
          </Box>
        </Container>
      </LayoutWithHeaderAndFooter>
    </>
  );
}
