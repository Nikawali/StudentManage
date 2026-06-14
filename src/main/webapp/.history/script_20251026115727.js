// 系统核心数据与逻辑封装
const StudentSystem = {
    // 1. 初始学生数据（模拟27条，符合题目要求）
    studentData: [
        { id: '12345675012', name: '小一', college: '计算机科学与工程学院', major: '软件工程', grade: 2015, class: '2', age: 21 },
        { id: '12345678912', name: '小二', college: '会计学院', major: '会计学', grade: 2015, class: '1', age: 19 },
        { id: '12345678912', name: '小三', college: '理学院', major: '数学', grade: 2015, class: '4', age: 20 },
        { id: '12345654912', name: '小四', college: '计算机科学与工程学院', major: '软件工程', grade: 2015, class: '2', age: 21 },
        { id: '12345670812', name: '小五', college: '会计学院', major: '会计学', grade: 2015, class: '1', age: 19 },
        { id: '12655678912', name: '小六', college: '理学院', major: '数学', grade: 2015, class: '2', age: 20 },
        { id: '12345678912', name: '小七', college: '计算机科学与工程学院', major: '软件工程', grade: 2015, class: '2', age: 21 },
        { id: '12345678912', name: '小八', college: '会计学院', major: '会计学', grade: 2015, class: '1', age: 19 },
        { id: '12345478912', name: '小九', college: '理学院', major: '数学', grade: 2015, class: '2', age: 20 },
        { id: '12345348912', name: '小十', college: '计算机科学与工程学院', major: '软件工程', grade: 2015, class: '2', age: 21 },
        { id: '12345678912', name: '小王', college: '会计学院', major: '会计学', grade: 2016, class: '3', age: 20 },
        { id: '12345678912', name: '小名', college: '理学院', major: '物理', grade: 2016, class: '1', age: 19 },
        { id: '10045678912', name: '小韩', college: '计算机科学与工程学院', major: '网络工程', grade: 2016, class: '2', age: 20 },
        { id: '12345678912', name: '小孙', college: '会计学院', major: '财务管理', grade: 2016, class: '1', age: 19 },
        { id: '12345678912', name: '小李', college: '理学院', major: '化学', grade: 2016, class: '3', age: 20 },
        { id: '12345600912', name: '小张', college: '计算机科学与工程学院', major: '软件工程', grade: 2016, class: '2', age: 21 },
        { id: '12345678912', name: '小赵', college: '会计学院', major: '会计学', grade: 2017, class: '1', age: 19 },
        { id: '10045678912', name: '小月', college: '理学院', major: '数学', grade: 2017, class: '2', age: 20 },
        { id: '12345679812', name: '小马', college: '计算机科学与工程学院', major: '软件工程', grade: 2017, class: '2', age: 21 },
        { id: '12340978912', name: '小刘', college: '会计学院', major: '会计学', grade: 2017, class: '1', age: 19 }


    ],

    // 2. 分页配置（每页10条，符合题目要求）
    pageConfig: {
        pageSize: 10,
        currentPage: 1,
        totalPage: 0 // 总页数由数据长度动态计算
    },

    // 3. 初始化总页数
    initTotalPage() {
        this.pageConfig.totalPage = Math.ceil(this.studentData.length / this.pageConfig.pageSize);
    },

    // 4. 获取当前页数据
    getCurrentPageData() {
        const { pageSize, currentPage } = this.pageConfig;
        const startIndex = (currentPage - 1) * pageSize;
        const endIndex = Math.min(startIndex + pageSize, this.studentData.length);
        return this.studentData.slice(startIndex, endIndex);
    },

    // 5. 更新分页信息显示（当前页、总条数、每页条数）
    updatePageInfo() {
        const { currentPage, pageSize } = this.pageConfig;
        const totalCount = this.studentData.length;
        document.getElementById('page-info').textContent =
            `第${currentPage}页，共${totalCount}条，(每页显示${pageSize}条)`;
        // 禁用/启用分页按钮（边界页控制）
        document.getElementById('prev-page').disabled = currentPage === 1;
        document.getElementById('next-page').disabled = currentPage === this.pageConfig.totalPage;
    },

    // 6. 渲染表格数据（含序号、隔行换色、hover效果）
    renderTable() {
        const tbody = document.getElementById('student-tbody');
        tbody.innerHTML = ''; // 清空表格
        const currentData = this.getCurrentPageData();
        const { currentPage, pageSize } = this.pageConfig;
        const startSerial = (currentPage - 1) * pageSize + 1; // 序号从当前页起始位置开始

        currentData.forEach((student, index) => {
            const tr = document.createElement('tr');
            const serialNum = startSerial + index; // 连续序号

            tr.innerHTML = `
                <td><input type="checkbox" class="stu-check" data-id="${student.id}"></td>
                <td>${serialNum}</td>
                <td>${student.id}</td>
                <td>${student.name}</td>
                <td>${student.college}</td>
                <td>${student.major}</td>
                <td>${student.grade}</td>
                <td>${student.class}</td>
                <td>${student.age}</td>
                <td>
                    <a href="javascript:;" class="operate-link view-link" data-index="${(currentPage - 1) * pageSize + index}">查看</a>
                    <a href="javascript:;" class="operate-link edit-link" data-index="${(currentPage - 1) * pageSize + index}">修改</a>
                </td>
            `;

            tbody.appendChild(tr);
        });

        // 绑定查看/修改链接的点击事件
        this.bindOperateLinkEvents();
    },

    // 7. 绑定查看/修改链接事件
    bindOperateLinkEvents() {
        const viewLinks = document.querySelectorAll('.view-link');
        const editLinks = document.querySelectorAll('.edit-link');
        const that = this; // 保存this指向

        // 查看事件：打开模态框，输入框禁用
        viewLinks.forEach(link => {
            link.addEventListener('click', function () {
                const index = this.getAttribute('data-index');
                const student = that.studentData[index];
                that.openModal('view', student, index);
            });
        });

        // 修改事件：打开模态框，输入框可编辑
        editLinks.forEach(link => {
            link.addEventListener('click', function () {
                const index = this.getAttribute('data-index');
                const student = that.studentData[index];
                that.openModal('edit', student, index);
            });
        });
    },

    // 8. 打开模态框（支持新增、修改、查看三种类型）
    openModal(type, student = {}, index = -1) {
        const modal = document.getElementById('operate-modal');
        const modalTitle = document.getElementById('modal-title');
        const operateType = document.getElementById('operate-type');
        const currentIndex = document.getElementById('current-index');
        const saveBtn = document.getElementById('save-btn');
        const formInputs = document.querySelectorAll('.form-input');

        // 设置模态框标题和操作类型
        operateType.value = type;
        currentIndex.value = index;
        switch (type) {
            case 'add':
                modalTitle.textContent = '新增学生信息';
                document.getElementById('operate-form').reset(); // 重置表单
                formInputs.forEach(input => input.disabled = false); // 启用输入框
                saveBtn.style.display = 'inline-block'; // 显示保存按钮
                break;
            case 'edit':
                modalTitle.textContent = '修改学生信息';
                this.fillFormData(student); // 填充现有数据
                formInputs.forEach(input => input.disabled = false); // 启用输入框
                saveBtn.style.display = 'inline-block'; // 显示保存按钮
                break;
            case 'view':
                modalTitle.textContent = '查看学生信息';
                this.fillFormData(student); // 填充现有数据
                formInputs.forEach(input => input.disabled = true); // 禁用输入框
                saveBtn.style.display = 'none'; // 隐藏保存按钮
                break;
        }

        // 显示模态框
        modal.classList.add('active');
    },

    // 9. 填充表单数据（用于修改和查看）
    fillFormData(student) {
        document.getElementById('stu-id').value = student.id || '';
        document.getElementById('stu-name').value = student.name || '';
        document.getElementById('stu-college').value = student.college || '';
        document.getElementById('stu-major').value = student.major || '';
        document.getElementById('stu-grade').value = student.grade || '';
        document.getElementById('stu-class').value = student.class || '';
        document.getElementById('stu-age').value = student.age || '';
    },

    // 10. 关闭模态框
    closeModal() {
        const modal = document.getElementById('operate-modal');
        modal.classList.remove('active');
    },

    // 11. 保存学生信息（新增/修改通用）
    saveStudent() {
        const operateType = document.getElementById('operate-type').value;
        const currentIndex = document.getElementById('current-index').value;
        // 获取表单输入数据
        const student = {
            id: document.getElementById('stu-id').value,
            name: document.getElementById('stu-name').value,
            college: document.getElementById('stu-college').value,
            major: document.getElementById('stu-major').value,
            grade: parseInt(document.getElementById('stu-grade').value),
            class: document.getElementById('stu-class').value,
            age: parseInt(document.getElementById('stu-age').value)
        };

        // 表单验证（合法性检查，加分项）
        if (!this.validateForm(student)) return;

        // 新增逻辑
        if (operateType === 'add') {
            this.studentData.push(student);
            alert('新增成功！');
        }
        // 修改逻辑
        else if (operateType === 'edit') {
            this.studentData[currentIndex] = student;
            alert('修改成功！');
        }

        // 重新计算分页并刷新表格
        this.initTotalPage();
        // 若新增后当前页超过总页数，跳转到最后一页
        if (this.pageConfig.currentPage > this.pageConfig.totalPage) {
            this.pageConfig.currentPage = this.pageConfig.totalPage;
        }
        this.renderTable();
        this.updatePageInfo();
        this.closeModal(); // 关闭模态框
    },

    // 12. 表单验证（合法性检查：学号、姓名、年龄等）
    validateForm(student) {
        // 学号：11位数字
        if (!/^\d{11}$/.test(student.id)) {
            alert('学号必须为11位数字！');
            return false;
        }
        // 姓名：2-4个汉字
        if (!/^[\u4e00-\u9fa5]{2,4}$/.test(student.name)) {
            alert('姓名必须为2-4个汉字！');
            return false;
        }
        // 学院/专业/班级：非空
        if (!student.college.trim() || !student.major.trim() || !student.class.trim()) {
            alert('学院、专业、班级不能为空！');
            return false;
        }
        // 年级：2000-2024之间的整数
        if (isNaN(student.grade) || student.grade < 2000 || student.grade > 2024) {
            alert('年级必须为2000-2024之间的整数！');
            return false;
        }
        // 年龄：15-30之间的整数
        if (isNaN(student.age) || student.age < 15 || student.age > 30) {
            alert('年龄必须为15-30之间的整数！');
            return false;
        }
        return true;
    },

    // 13. 删除选中学生（支持单选/多选/全选删除）
    deleteSelectedStudents() {
        const checkedBoxes = document.querySelectorAll('.stu-check:checked');
        if (checkedBoxes.length === 0) {
            alert('请选择要删除的学生！');
            return;
        }
        // 确认删除
        if (!confirm(`确定要删除选中的${checkedBoxes.length}条学生信息吗？`)) {
            return;
        }

        // 获取选中的学生学号，过滤删除后的数据
        const checkedIds = Array.from(checkedBoxes).map(box => box.getAttribute('data-id'));
        this.studentData = this.studentData.filter(student => !checkedIds.includes(student.id));

        // 重新计算分页并刷新表格
        this.initTotalPage();
        // 处理页码越界（如删除最后一页所有数据，跳转到前一页）
        if (this.pageConfig.currentPage > this.pageConfig.totalPage && this.pageConfig.totalPage > 0) {
            this.pageConfig.currentPage = this.pageConfig.totalPage;
        } else if (this.pageConfig.totalPage === 0) {
            this.pageConfig.currentPage = 1;
        }
        this.renderTable();
        this.updatePageInfo();
        // 取消全选状态
        document.getElementById('select-all').checked = false;

        alert(`成功删除${checkedBoxes.length}条学生信息！`);
    },

    // 14. 全选/取消全选
    toggleSelectAll() {
        const selectAll = document.getElementById('select-all');
        const checkboxes = document.querySelectorAll('.stu-check');
        checkboxes.forEach(checkbox => {
            checkbox.checked = selectAll.checked;
        });
    },

    // 15. 分页切换（上一页/下一页）
    changePage(direction) {
        const { currentPage, totalPage } = this.pageConfig;
        if (direction === 'prev') {
            // 上一页：当前页>1时可切换
            if (currentPage > 1) {
                this.pageConfig.currentPage = currentPage - 1;
            } else {
                alert('已经是第一页了！');
            }
        } else if (direction === 'next') {
            // 下一页：当前页<总页数时可切换
            if (currentPage < totalPage) {
                this.pageConfig.currentPage = currentPage + 1;
            } else {
                alert('已经是最后一页了！');
            }
        }
        // 刷新表格和分页信息
        this.renderTable();
        this.updatePageInfo();
    },

    // 16. 绑定所有页面交互事件
    bindEvents() {
        const that = this; // 保存this指向

        // 新增按钮：打开新增模态框
        document.getElementById('add-btn').addEventListener('click', () => {
            that.openModal('add');
        });

        // 删除按钮：删除选中学生
        document.getElementById('delete-btn').addEventListener('click', () => {
            that.deleteSelectedStudents();
        });

        // 全选复选框：选中/取消选中当前页所有学生
        document.getElementById('select-all').addEventListener('change', () => {
            that.toggleSelectAll();
        });

        // 分页按钮：上一页/下一页
        document.getElementById('prev-page').addEventListener('click', () => {
            that.changePage('prev');
        });
        document.getElementById('next-page').addEventListener('click', () => {
            that.changePage('next');
        });

        // 模态框关闭：右上角关闭按钮/取消按钮
        document.getElementById('close-modal').addEventListener('click', () => {
            that.closeModal();
        });
        document.getElementById('cancel-btn').addEventListener('click', () => {
            that.closeModal();
        });

        // 保存按钮：新增/修改学生信息
        document.getElementById('save-btn').addEventListener('click', () => {
            that.saveStudent();
        });

        // 点击模态框外部关闭模态框
        document.getElementById('operate-modal').addEventListener('click', function (e) {
            if (e.target === this) {
                that.closeModal();
            }
        });
    },

    // 17. 系统初始化（入口函数）
    init() {
        this.initTotalPage(); // 计算总页数
        this.renderTable(); // 渲染初始表格
        this.updatePageInfo(); // 显示初始分页信息
        this.bindEvents(); // 绑定所有交互事件
    }
};

// 页面加载完成后初始化系统
document.addEventListener('DOMContentLoaded', function () {
    StudentSystem.init();
});