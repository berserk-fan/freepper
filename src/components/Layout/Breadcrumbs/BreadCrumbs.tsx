import React from 'react';
import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import {useRouter} from "next/router";
import Link from "next/link";
import {Page, pages} from "../Header/pages";

function prefixes<T>(arr: T[]): T[][] {
    return arr.map((_, idx) => arr.slice(0, idx + 1));
}

function toPath(x: string[]): string {
    return x.length === 1 ? "/" : x.join("/")
}

const hrefToPage: Record<string, Page> = Object.fromEntries(Object.values(pages).map((page):[string, Page] => [page.path, page]))
function toName(pathAsString: string, path: string[]): string {
    const last = path[path.length -1];
    const predefinedPage = hrefToPage[pathAsString];
    if(predefinedPage) {
        return predefinedPage.name;
    } else {
        return last;
    }
}

function pathNeeded(path: string[]): boolean {
    const prevLast = path[path.length - 2];
    const last = path[path.length - 1];
    return !(prevLast === "categories" || last === "categories");
}

//overrides: /collection/element_125 + overrides == {element_125: "My Element"} = /collection/My Element
export default function BreadCrumbs({overrides = {}} : {overrides?: Record<string, string>}) {
    const router = useRouter();
    const [path] = router.asPath.split("?");
    const sections = path === "/" ? [""] : path.split("/");
    const paths = prefixes(sections).filter(pathNeeded);
    return (
        <Breadcrumbs aria-label="breadcrumb">
            {paths.map(pathArr => {
                const pathAsString = toPath(pathArr);
                const name = overrides[pathArr[pathArr.length - 1]] || toName(pathAsString, pathArr);
                return <Link href={pathAsString}>{name}</Link>;
            })}
        </Breadcrumbs>
    );
}
