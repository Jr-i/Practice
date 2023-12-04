onload = function () {
    const table_1 = document.getElementById("table");
    const rows = table_1.rows;
    for (let i = 1; i < rows.length - 1; i++) {
        const tr = rows[i];
        trEvent(tr);
    }

    // 新增数据功能
    const submit = document.getElementById("addButton");
    submit.onclick = addLine;

}

function trEvent(tr) {
    tr.onmouseover = showBGColor;
    tr.onmouseout = clearBGColor;

    const td_price = tr.cells[1];
    if (td_price.tagName === "TD") {
        td_price.onmouseover = showHand;
        td_price.onclick = edit;
    }

    const td_amount = tr.cells[2];
    if (td_amount.tagName === "TD") {
        td_amount.onmouseover = showHand;
        td_amount.onclick = edit;
    }

    const deleteImg = tr.cells[4].firstChild;
    deleteImg.onclick = deleteLine;
}


function addLine() {
/*
    const table_2 = document.getElementById("newTable");
    const lines = table_2.rows;

    const name = lines[0].cells[1].firstChild.value;
    const price = lines[1].cells[1].firstChild.value;
    const count = lines[2].cells[1].firstChild.value;
*/

    const table_1 = document.getElementById("table");

    const newTableRow = table_1.insertRow(table_1.rows.length - 1);
    newTableRow.insertCell(0).innerText = name;
    newTableRow.insertCell(1).innerText = price;
    newTableRow.insertCell(2).innerText = count;
    newTableRow.insertCell(3).innerText = parseInt(price) * parseInt(count);
    newTableRow.insertCell(4).innerHTML = '<img src="img/delete.png" class="deleteImg" />';

    updateSumTotal();

    trEvent(newTableRow);

}

function showBGColor() {
    const td = event.target;
    const tr = td.parentElement;
    tr.style.backgroundColor = "gray";
    const tds = tr.cells;
    for (let i = 0; i < tds.length; i++) {
        tds[i].style.color = 'white';
    }
}

function clearBGColor() {
    const td = event.target;
    const tr = td.parentElement;
    tr.style.backgroundColor = "transparent";
    const tds = tr.cells;
    for (let i = 0; i < tds.length; i++) {
        tds[i].style.color = 'black';
    }
}

function showHand() {
    const td = event.target;
    td.style.cursor = "pointer";
}

function edit() {
    const td = event.target;
    // nodeType:1——标签,2——属性,3——文本
    if (td.firstChild.nodeType === 3) {
        const oldValue = td.innerText;
        td.innerHTML = "<input type='text' size='2'/>";
        let input = td.firstChild;
        input.value = oldValue;
        input.select();
        // 失去焦点，更新数据
        input.onkeydown = checkInput;
        input.onblur = update;
    }
}

function checkInput() {
    const input = event.keyCode;

    // 非数字、删除键
    if (!((input >= 48 && input <= 57) || input === 8)) {
        // 输入不生效
        event.preventDefault();

    }

    // 回车
    if (input === 13) {
        // 触发失去焦点事件
        event.target.blur();
    }
}

function update() {
    const input = event.target;
    const newPrice = input.value;
    const td = input.parentElement;
    td.innerText = newPrice;

    // 更新当前行的小计
    updateSumLine(td.parentElement);
}

function updateSumLine(tr) {
    const tds = tr.cells;
    // 获取到的是字符串类型
    const price = tds[1].innerText;
    const count = tds[2].innerText;
    tds[3].innerText = parseInt(price) * parseInt(count);

    updateSumTotal();
}

function updateSumTotal() {
    const table_1 = document.getElementById("table");
    const rows = table_1.rows;
    let sum = 0;
    let lineSum;
    for (let i = 1; i < rows.length - 1; i++) {
        lineSum = rows[i].cells[3].innerText;
        sum += parseInt(lineSum);
    }

    rows[rows.length - 1].cells[1].innerText = sum;
}

function deleteLine() {
    // alert 只有确认选项
    // confirm 有确认和取消两个选项
    if (window.confirm("是否删除当前记录？")) {
        const table_1 = document.getElementById("table");
        const img = event.target;
        const tr = img.parentElement.parentElement;
        table_1.deleteRow(tr.rowIndex);

        updateSumTotal();
    }

}



