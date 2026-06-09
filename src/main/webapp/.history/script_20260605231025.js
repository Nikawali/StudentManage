const BASE_URL =
    "http://localhost:8080/test5_war_exploded/student";
    
    function getToken() {

    return localStorage.getItem(
        "token"
    );
}

function authFetch(
    url,
    options = {}
) {

    options.headers = {

        ...(options.headers || {}),

        token:
            getToken()
    };

    return fetch(
        url,
        options
    );
}
window.onload = function () {

    loadStudents();

    bindButtonEvent();
};

/**
 * 查询全部学生
 */
function loadStudents() {

    authFetch(BASE_URL + "?action=findAll")

        .then(response => response.json())

        .then(result => {
            console.log(result);
             if (result.code !== 200) {

                alert(result.message);

                return;
            }


            let html = "";

            result.data.forEach(student => {

                html += `
<tr>

    <td>
        <input
            type="checkbox"
            class="row-checkbox"
            data-id="${student.studentId}"
        >
    </td>

    <td>${student.studentId}</td>

    <td>${student.name}</td>

    <td>${student.gender}</td>

    <td>${student.age}</td>

    <td>${student.phone}</td>

    <td>${student.classId}</td>

    <td>${student.college}</td>

    <td>${student.major}</td>

<td>

    <a
        href="#"
        class="operate-link edit-btn"
        data-id="${student.studentId}"
        data-name="${student.name}"
        data-gender="${student.gender}"
        data-age="${student.age}"
        data-phone="${student.phone}"
        data-classid="${student.classId}"
        data-college="${student.college}"
        data-major="${student.major}">
        编辑
    </a>

    <a
        href="#"
        class="operate-link delete-one-btn"
        data-id="${student.studentId}">
        删除
    </a>

</td>

</tr>
`;
            });

            document.getElementById(
                "studentBody"
            ).innerHTML = html;

            bindCheckboxEvent();

            bindDeleteEvent();

            bindEditEvent();
        })

        .catch(error => {

            console.error(error);

            alert("获取学生数据失败");
        });
}

/**
 * 按钮事件
 */
function bindButtonEvent() {

    document.querySelector(".add-btn")
        .onclick = addStudent;

    document.querySelector(".delete-btn")
        .onclick = batchDelete;

    document.querySelector(".search-btn")
        .onclick = searchStudent;
}

/**
 * 全选事件
 */
function bindCheckboxEvent() {

    const selectAll =
        document.getElementById("selectAll");

    const rowCheckboxes =
        document.querySelectorAll(".row-checkbox");

    selectAll.onchange = function () {

        rowCheckboxes.forEach(item => {

            item.checked =
                selectAll.checked;
        });
    };

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

/**
 * 新增学生
 */
function addStudent() {

    const studentId =
        prompt("请输入学号");

    if (!studentId) return;

    const name =
        prompt("请输入姓名");

    if (!name) return;

    const gender =
        prompt("请输入性别");

    if (!gender) return;

    const age =
        prompt("请输入年龄");

    if (!age) return;

    const phone =
        prompt("请输入手机号");

    if (!phone) return;

    const classId =
        prompt("请输入班级ID");

    if (!classId) return;

    const college =
        prompt("请输入学院");

    if (!college) return;

    const major =
        prompt("请输入专业");

    if (!major) return;

    const params =
        new URLSearchParams();

    params.append(
        "action",
        "add"
    );

    params.append(
        "studentId",
        studentId
    );

    params.append(
        "name",
        name
    );

    params.append(
        "gender",
        gender
    );

    params.append(
        "age",
        age
    );

    params.append(
        "phone",
        phone
    );

    params.append(
        "classId",
        classId
    );

    params.append(
        "college",
        college
    );

    params.append(
        "major",
        major
    );

    authFetch(BASE_URL, {

        method: "POST",

        body: params
    })
        .then(response => response.json())

        .then(result => {

            alert(result.message);

            loadStudents();
        })

        .catch(error => {

            console.error(error);

            alert("新增失败");
        });
}

/**
 * 单个删除
 */
function bindDeleteEvent() {

    document
        .querySelectorAll(
            ".delete-one-btn"
        )
        .forEach(btn => {

            btn.onclick =
                function (e) {

                    e.preventDefault();

                    const studentId =
                        this.dataset.id;

                    if (
                        !confirm(
                            "确定删除该学生？"
                        )
                    ) {
                        return;
                    }

                    deleteStudent(
                        studentId
                    );
                };
        });
}

/**
 * 删除学生
 */
function deleteStudent(studentId) {

    const params =
        new URLSearchParams();

    params.append(
        "action",
        "deleteStudent"
    );

    params.append(
        "studentId",
        studentId
    );

    authFetch(BASE_URL, {

        method: "POST",

        headers: {
            "Content-Type":
                "application/x-www-form-urlencoded"
        },

        body: params.toString()

    })
        .then(response => response.json())

        .then(result => {

            if (result.code === 200) {

                alert(result.message);

                loadStudents();

            } else {

                alert(result.message);
            }

        })

        .catch(error => {

            console.error(error);

            alert("删除失败");
        });
}
/**
 * 批量删除
 */
async function batchDelete() {

    const checkedList =
        document.querySelectorAll(
            ".row-checkbox:checked"
        );

    if (checkedList.length === 0) {
        alert("请选择学生");
        return;
    }

    if (!confirm("确定删除选中的学生？")) {
        return;
    }

    for (const item of checkedList) {

        const params =
            new URLSearchParams();

        params.append(
            "action",
            "deleteStudent"
        );

        params.append(
            "studentId",
            item.dataset.id
        );

        await authFetch(BASE_URL, {
            method: "POST",
            body: params
        });
    }

    alert("删除成功");

    loadStudents();
}

function bindEditEvent() {

    document
        .querySelectorAll(".edit-btn")
        .forEach(btn => {

            btn.onclick = function (e) {

                e.preventDefault();

                const studentId =
                    this.dataset.id;

                const name =
                    prompt(
                        "姓名",
                        this.dataset.name
                    );

                if (!name) return;

                const gender =
                    prompt(
                        "性别",
                        this.dataset.gender
                    );

                if (!gender) return;

                const age =
                    prompt(
                        "年龄",
                        this.dataset.age
                    );

                if (!age) return;

                const phone =
                    prompt(
                        "手机号",
                        this.dataset.phone
                    );

                if (!phone) return;

                const classId =
                    prompt(
                        "班级ID",
                        this.dataset.classid
                    );

                if (!classId) return;

                const college =
                    prompt(
                        "学院",
                        this.dataset.college
                    );

                if (!college) return;

                const major =
                    prompt(
                        "专业",
                        this.dataset.major
                    );

                if (!major) return;

                updateStudent(
                    studentId,
                    name,
                    gender,
                    age,
                    phone,
                    classId,
                    college,
                    major
                );
            };
        });
}

function updateStudent(
    studentId,
    name,
    gender,
    age,
    phone,
    classId,
    college,
    major
) {

    const params =
        new URLSearchParams();

    params.append(
        "action",
        "update"
    );

    params.append(
        "studentId",
        studentId
    );

    params.append(
        "name",
        name
    );

    params.append(
        "gender",
        gender
    );

    params.append(
        "age",
        age
    );

    params.append(
        "phone",
        phone
    );

    params.append(
        "classId",
        classId
    );

    params.append(
        "college",
        college
    );

    params.append(
        "major",
        major
    );

    authFetch(BASE_URL, {

        method: "POST",

        headers: {
            "Content-Type":
                "application/x-www-form-urlencoded"
        },

        body: params.toString()
    })

        .then(response => response.json())

        .then(result => {

            alert(result.message);

            if (result.code === 200) {

                loadStudents();
            }
        })

        .catch(error => {

            console.error(error);

            alert("修改失败");
        });
}

function searchStudent() {

    const type = prompt(
        "请输入查询方式：\n1-学号精准查询\n2-模糊查询"
    );

    if (!type) return;

    // 学号查询
    if (type === "1") {

        const studentId =
            prompt("请输入学号");

        if (!studentId) return;

        authFetch(
            BASE_URL +
            "?action=findById&studentId=" +
            studentId
        )

            .then(response => response.json())

            .then(result => {

                if (result.code !== 200) {

                    alert(result.message);
                    return;
                }

                const student =
                    result.data;

                document.getElementById(
                    "studentBody"
                ).innerHTML = `
<tr>

<td>
<input
type="checkbox"
class="row-checkbox"
data-id="${student.studentId}">
</td>

<td>${student.studentId}</td>

<td>${student.name}</td>

<td>${student.gender}</td>

<td>${student.age}</td>

<td>${student.phone}</td>

<td>${student.classId}</td>

<td>${student.college}</td>

<td>${student.major}</td>

<td>

<a href="#"
class="operate-link edit-btn"
data-id="${student.studentId}"
data-name="${student.name}"
data-gender="${student.gender}"
data-age="${student.age}"
data-phone="${student.phone}"
data-classid="${student.classId}"
data-college="${student.college}"
data-major="${student.major}">
编辑
</a>

<a href="#"
class="operate-link delete-one-btn"
data-id="${student.studentId}">
删除
</a>

</td>

</tr>
`;
                bindCheckboxEvent();
                bindDeleteEvent();
                bindEditEvent();

            })


            .catch(error => {

                console.error(error);

                alert("查询失败");
            });
    }

    // 模糊查询
    else if (type === "2") {

        const keyword =
            prompt("请输入关键字");

        if (!keyword) return;

        authFetch(
            BASE_URL +
            "?action=search&keyword=" +
            encodeURIComponent(keyword)
        )

            .then(response => response.json())

            .then(result => {

                if (result.code !== 200) {

                    alert(result.message);
                    return;
                }

                let html = "";

                result.data.forEach(student => {

                    html += `
<tr>

<td>
<input
type="checkbox"
class="row-checkbox"
data-id="${student.studentId}">
</td>

<td>${student.studentId}</td>

<td>${student.name}</td>

<td>${student.gender}</td>

<td>${student.age}</td>

<td>${student.phone}</td>

<td>${student.classId}</td>

<td>${student.college}</td>

<td>${student.major}</td>

<td>

<a href="#"
class="operate-link edit-btn"
data-id="${student.studentId}"
data-name="${student.name}"
data-gender="${student.gender}"
data-age="${student.age}"
data-phone="${student.phone}"
data-classid="${student.classId}"
data-college="${student.college}"
data-major="${student.major}">
编辑
</a>

<a href="#"
class="operate-link delete-one-btn"
data-id="${student.studentId}">
删除
</a>

</td>

</tr>
`;
                });

                document.getElementById(
                    "studentBody"
                ).innerHTML = html;

                bindCheckboxEvent();
                bindDeleteEvent();
                bindEditEvent();

            })

            .catch(error => {

                console.error(error);

                alert("查询失败");
            });
    }

    else {

        alert("请输入1或2");
    }
}