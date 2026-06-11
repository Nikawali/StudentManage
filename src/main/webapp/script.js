const BASE_URL="http://localhost:8080/test5_war_exploded/student"
const currentRole = localStorage.getItem("role");

function getToken() {
    return localStorage.getItem("token");
}

function authFetch(
    url,
    options = {}
) {
    options.headers = {
        ...(options.headers || {}),
        token: getToken()
    };
    return fetch(url, options);
}

window.onload = function () {
    if (currentRole !== "teacher" && currentRole !== "admin") {
        alert("当前页面无访问权限，请重新登录");
        location.href = "login.html";
        return;
    }
    const urlParams = new URLSearchParams(window.location.search);
    const classId = urlParams.get("classId");

    if (classId) {
        searchStudentByClassId(classId);
    } else {
        loadStudents();
    }

    initPageByRole(classId);
    bindButtonEvent();
};

function loadStudents() {
    authFetch(BASE_URL + "?action=findAll")
        .then(response => response.json())
        .then(result => {
            console.log(result);
            if (result.code !== 200) {
                alert(result.message);
                return;
            }

            document.getElementById("studentBody").innerHTML = renderStudentRows(result.data);
            bindCheckboxEvent();
            bindDeleteEvent();
            bindEditEvent();
        })
        .catch(error => {
            console.error(error);
            alert("获取学生数据失败");
        });
}

function searchStudentByClassId(classId) {
    authFetch(
        BASE_URL + "?action=findByClassId&classId=" + classId
    )
    .then(response => response.json())
    .then(result => {
        alert("当前查看：班级ID " + classId + " 的学生");
        alert(result.message);
        if (result.code !== 200) {
            return;
        }

        document.getElementById("studentBody").innerHTML = renderStudentRows(result.data);
        bindCheckboxEvent();
        bindDeleteEvent();
        bindEditEvent();
    })
    .catch(error => {
        console.error(error);
        alert("加载班级学生失败");
    });
}

function bindButtonEvent() {
    document.querySelector(".add-btn").onclick = addStudent;
    document.querySelector(".delete-btn").onclick = batchDelete;
    document.querySelector(".search-btn").onclick = searchStudent;
    if (currentRole !== "admin") {
        document.querySelector(".add-btn").style.display = "none";
    }
}

function bindCheckboxEvent() {
    const selectAll = document.getElementById("selectAll");
    const rowCheckboxes = document.querySelectorAll(".row-checkbox");

    selectAll.onchange = function () {
        rowCheckboxes.forEach(item => {
            item.checked = selectAll.checked;
        });
    };

    rowCheckboxes.forEach(item => {
        item.onchange = function () {
            const checkedCount = document.querySelectorAll(".row-checkbox:checked").length;
            selectAll.checked = checkedCount === rowCheckboxes.length;
        };
    });
}

// ====================== 新增学生：classId 不能为空 ======================
function addStudent() {
    if (currentRole !== "admin") {
        alert("仅管理员可新增学生");
        return;
    }
    const studentId = prompt("请输入学号");
    if (!studentId) return;

    const name = prompt("请输入姓名");
    if (!name) return;

    const gender = prompt("请输入性别");
    if (!gender) return;

    const age = prompt("请输入年龄");
    if (!age) return;

    const phone = prompt("请输入手机号");
    if (!phone) return;

    let classId;
    while (true) {
        classId = resolveClassIdForEdit();
        if (classId && classId.trim() !== "") {
            break;
        }
        alert("班级ID不能为空，请重新输入！");
    }

    const params = new URLSearchParams();
    params.append("action", "add");
    params.append("studentId", studentId);
    params.append("name", name);
    params.append("gender", gender);
    params.append("age", age);
    params.append("phone", phone);
    params.append("classId", classId);
    authFetch(BASE_URL, {
        method: "POST",
        body: params
    })
    .then(response => response.json())
    .then(result => {
        alert(result.message);
        if (result.code !== 200) {
            return;
        }
        loadStudents();
    })
    .catch(error => {
        console.error(error);
        alert("新增失败");
    });
}

function bindDeleteEvent() {
    document.querySelectorAll(".delete-one-btn").forEach(btn => {
        btn.onclick = function (e) {
            e.preventDefault();
            const studentId = this.dataset.id;
            if (!confirm("确定删除该学生？")) {
                return;
            }
            deleteStudent(studentId);
        };
    });
}

function deleteStudent(studentId) {
    const params = new URLSearchParams();
    params.append("action", "deleteStudent");
    params.append("studentId", studentId);

    authFetch(BASE_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
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

async function batchDelete() {
    const checkedList = document.querySelectorAll(".row-checkbox:checked");
    if (checkedList.length === 0) {
        alert("请选择学生");
        return;
    }

    if (!confirm("确定删除选中的学生？")) {
        return;
    }

    for (const item of checkedList) {
        const params = new URLSearchParams();
        params.append("action", "deleteStudent");
        params.append("studentId", item.dataset.id);

        const response = await authFetch(BASE_URL, {
            method: "POST",
            body: params
        });
        
        const result = await response.json();

        if (result.code !== 200) {
            alert(result.message);
            return;
        }
    }

    alert("删除成功");
    loadStudents();
}

// ====================== 修改学生：classId 不能为空 ======================
function bindEditEvent() {
    document.querySelectorAll(".edit-btn").forEach(btn => {
        btn.onclick = function (e) {
            e.preventDefault();
            const studentId = this.dataset.id;
            const name = prompt("姓名", this.dataset.name);
            if (!name) return;

            const gender = prompt("性别", this.dataset.gender);
            if (!gender) return;

            const age = prompt("年龄", this.dataset.age);
            if (!age) return;

            const phone = prompt("手机号", this.dataset.phone);
            if (!phone) return;

            let classId = this.dataset.classid;
            if (currentRole === "admin") {
                while (true) {
                    classId = prompt("班级ID", this.dataset.classid);
                    if (classId && classId.trim() !== "") {
                        break;
                    }
                    alert("班级ID不能为空，请重新输入！");
                }
            }

            updateStudent(
                studentId,
                name,
                gender,
                age,
                phone,
                classId,
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
) {
    const params = new URLSearchParams();
    params.append("action", "update");
    params.append("studentId", studentId);
    params.append("name", name);
    params.append("gender", gender);
    params.append("age", age);
    params.append("phone", phone);
    params.append("classId", classId);

    authFetch(BASE_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
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

// ====================== 查询学生（按班级）：classId 不能为空 ======================
function searchStudent() {
    const tip = currentRole === "admin"
        ? "请输入查询方式：\n1-学号精准查询\n2-模糊查询\n3-班级ID查询"
        : "请输入查询方式：\n1-本班学号精准查询\n2-本班模糊查询";
    const type = prompt(tip);
    if (!type) return;

    if (type === "1") {
        const studentId = prompt("请输入学号");
        if (!studentId) return;

        authFetch(
            BASE_URL + "?action=findById&studentId=" + studentId
        )
        .then(response => response.json())
        .then(result => {
            alert(result.message);
            if (result.code !== 200) {
                return;
            }

            const student = result.data;
            document.getElementById("studentBody").innerHTML = renderStudentRows([student]);
            bindCheckboxEvent();
            bindDeleteEvent();
            bindEditEvent();
        })
        .catch(error => {
            console.error(error);
            alert("查询失败");
        });
    }
    else if (type === "2") {
        const keyword = prompt("请输入关键字");
        if (!keyword) return;

        authFetch(
            BASE_URL + "?action=search&keyword=" + encodeURIComponent(keyword)
        )
        .then(response => response.json())
        .then(result => {
            alert(result.message);
            if (result.code !== 200) {
                return;
            }

            document.getElementById("studentBody").innerHTML = renderStudentRows(result.data);
            bindCheckboxEvent();
            bindDeleteEvent();
            bindEditEvent();
        })
        .catch(error => {
            console.error(error);
            alert("查询失败");
        });
    } else if (type === "3" && currentRole === "admin") {
        let classId;
        while (true) {
            classId = prompt("请输入班级ID");
            if (classId && classId.trim() !== "") {
                break;
            }
            alert("班级ID不能为空，请重新输入！");
        }
        searchStudentByClassId(classId);
    }
    else {
        alert(currentRole === "admin" ? "请输入1或2或3" : "请输入1或2");
    }
}

// ====================== 获取班级ID：prompt 不能为空 ======================
function resolveClassIdForEdit() {
    const urlParams = new URLSearchParams(window.location.search);
    const pageClassId = urlParams.get("classId");
    if (pageClassId) {
        return pageClassId;
    }
    if (currentRole === "teacher") {
        const teacherClassId = localStorage.getItem("teacherClassId");
        if (teacherClassId) {
            return teacherClassId;
        }
    }

    let classId;
    while (true) {
        classId = prompt("请输入班级ID");
        if (classId && classId.trim() !== "") {
            break;
        }
        alert("班级ID不能为空，请重新输入！");
    }
    return classId;
}

function initPageByRole(classId) {
    const pageTitle = document.getElementById("pageTitle");
    const pageInfo = document.getElementById("pageInfo");
    const deleteBtn = document.querySelector(".delete-btn");
    const searchBtn = document.querySelector(".search-btn");

    if (!pageTitle || !pageInfo) {
        return;
    }

    if (currentRole === "admin") {
        pageTitle.innerText = classId ? "班级学生管理" : "学生管理系统";
        pageInfo.innerText = classId ? "班级学生列表" : "全部学生列表";
        if (deleteBtn) {
            deleteBtn.innerText = "删除";
        }
        if (searchBtn) {
            searchBtn.innerText = "查询";
        }
        return;
    }

    pageTitle.innerText = classId ? "本班学生管理" : "学生管理系统";
    pageInfo.innerText = "本班学生列表";
    if (deleteBtn) {
        deleteBtn.innerText = "删除本班学生";
    }
    if (searchBtn) {
        searchBtn.innerText = "查询本班学生";
    }
}

function renderStudentRows(students) {
    let html = "";
    students.forEach(student => {
        const classId = student.classId == null ? "" : student.classId;
        const college = student.college == null ? "" : student.college;
        const major = student.major == null ? "" : student.major;
        html += `
<tr>
    <td><input type="checkbox" class="row-checkbox" data-id="${student.studentId}"></td>
    <td>${student.studentId}</td>
    <td>${student.name}</td>
    <td>${student.gender}</td>
    <td>${student.age}</td>
    <td>${student.phone}</td>
    <td>${classId}</td>
    <td>${college}</td>
    <td>${major}</td>
    <td>
        <a href="#" class="operate-link edit-btn"
           data-id="${student.studentId}"
           data-name="${student.name}"
           data-gender="${student.gender}"
           data-age="${student.age}"
           data-phone="${student.phone}"
           data-classid="${classId}"
           data-college="${college}"
           data-major="${major}">编辑</a>
        <a href="#" class="operate-link delete-one-btn" data-id="${student.studentId}">删除</a>
    </td>
</tr>`;
    });
    return html;
}