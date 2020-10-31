import Header from "./Header/Header";

export default function LayoutWithHeader({children}) {
    return (
        <>
            <Header/>
            {children}
        </>
    )
};
