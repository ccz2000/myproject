// 日期格式化函数
function dateFormat(date, fmt = 'YYYY-MM-DD HH:mm:ss') {
    if (!date) return '';
    date = new Date(date);
    let ret;
    const opt = {
        "Y+": date.getFullYear().toString(),
        "M+": (date.getMonth() + 1).toString(),
        "D+": date.getDate().toString(),
        "H+": date.getHours().toString(),
        "m+": date.getMinutes().toString(),
        "s+": date.getSeconds().toString()
    };
    for (let k in opt) {
        ret = new RegExp("(" + k + ")").exec(fmt);
        if (ret) {
            fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")));
        }
    }
    return fmt;
}

// 货币格式化函数
function currency(value, currency = 'CNY', decimals = 2) {
    if (!value) return '0.00';
    value = parseFloat(value);
    return new Intl.NumberFormat('zh-CN', {
        style: 'currency',
        currency: currency,
        minimumFractionDigits: decimals
    }).format(value);
}

window.filters = {
    dateFormat,
    currency
}; 