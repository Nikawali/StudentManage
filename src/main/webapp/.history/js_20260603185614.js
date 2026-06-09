function showTempTip(message, color) {
    const tip = document.createElement('div');
    tip.style.position = 'fixed';
    tip.style.top = '550px';
    tip.style.left = '50%';
    tip.style.transform = 'translateX(-50%)';
    tip.style.padding = '10px 20px';
    tip.style.borderRadius = '18px';
    tip.style.zIndex = 99999;
    tip.style.color = 'white';
    tip.style.backgroundColor = color;
    tip.style.opacity = '1';
    tip.style.transition = 'opacity 0.5s ease';
    tip.innerText = message;
    document.body.appendChild(tip);

    // 2 秒后开始渐隐
    setTimeout(() => {
        tip.style.opacity = '0';
        // 动画结束后删除元素
        setTimeout(() => tip.remove(), 500);
    }, 2000);
}