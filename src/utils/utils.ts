function renderPrimitive(p: any) {
  switch (typeof p) {
    case "string":
      return `<span class="string">"${p}"</span>`;
    case "number":
      return `<span class="number">${p}</span>`;
    default:
      return `${p}`;
  }
}

function renderJSON(obj: any, depth: number = 0, indentation: string = "  ") {
  let retValue = "";
  if (typeof obj !== "object") {
    return ` ${  renderPrimitive(obj)}`;
  }
  for (const [key, value] of Object.entries(obj)) {
    retValue +=
      `<div class='tree'>${ 
        indentation.repeat(depth) 
      }<span class="key">${key}</span>:`;
    retValue += renderJSON(value, depth + 1);
    retValue += "</div>";
  }
  return retValue;
}

export function renderJSONPlusCss(obj: any) {
  const yaml = renderJSON(obj);
  return `
        <style>
        code > .tree {
            margin-left: 0
        }
        .key {
            color: orange;
        }
        .number {
            color: cornflowerblue;
        }
        .string {
            color: forestgreen;
        }
        code {
            white-space: -moz-pre-wrap;
            white-space: -pre-wrap;
            white-space: -o-pre-wrap;
            white-space: pre-wrap; 
            word-wrap: break-word; /* Internet Explorer 5.5+ */  
        }
        </style>
        <code>${yaml}</code>
        
    `;
}
