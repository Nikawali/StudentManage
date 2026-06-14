const BASE_URL =
    "http://localhost:8080/test5_war_exploded/student";
window.onload = function () {

    loadStudents();

};

function loadStudents() {

    fetch(BASE_URL + "?action=findAll")

        .then(response => response.json())

        .then(result => {

            console.log(result);

            let html = "";

            result.data.forEach(student => {

   html += `
<tr>

    <td>
        <input
            type="checkbox"
            class="row-checkbox"
            data-id="${student.id}"
        >
    </td>

    <td>${student.id}</td>

    <td>${student.name}</td>

    <td>${student.gender}</td>

    <td>${student.age}</td>

    <td>${student.phone}</td>

    <td>${student.department || ""}</td>

    <td>

        <a href="#" class="operate-link">
            查看
        </a>

        <a href="#" class="operate-link">
            编辑
        </a>

        <a href="#" class="operate-link">
            删除
        </a>

    </td>

</tr>
`;
            });

            document.getElementById("studentBody").innerHTML =
                html;
                bindCheckboxEvent();
        })

        .catch(error => {

            console.error(error);

            alert("获取学生数据失败");

        });
}

function bindCheckboxEvent() {

    const selectAll =
        document.getElementById("selectAll");

    const rowCheckboxes =
        document.querySelectorAll(".row-checkbox");

    // 全选
    selectAll.onchange = function () {

        rowCheckboxes.forEach(item => {

            item.checked =
                selectAll.checked;

        });

    };

    // 单个选择
    rowCheckboxes.forEach(item => {

        item.onchange = function () {

            const checkedCount =
                document.querySelectorAll(
                    ".row-checkbox:checked"
                ).length;

            selectAll.checked =
                checkedCount ===
                rowCheckboxes.length;

        };

    });

}