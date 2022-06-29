import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import LayoutWithHeaderAndFooter from "../components/Layout/LayoutWithHeaderAndFooter";

export default function Custom404() {
  return (
    <>
      <LayoutWithHeaderAndFooter disableBreadcrumbs>
        <Box
          width="100vw"
          height="400px"
          className="center"
          display="flex"
          flexDirection="column"
          justifyContent="center"
          alignItems="center"
        >
          <Typography variant="h2" component="h1">
            Страница не найдена
          </Typography>
        </Box>
      </LayoutWithHeaderAndFooter>
    </>
  );
}
