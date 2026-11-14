(function() {
  var style = getComputedStyle(document.documentElement);
  var accent = style.getPropertyValue('--accent').trim() || '#3B82C4';
  var accent2 = style.getPropertyValue('--accent2').trim() || '#E8923C';
  var ink = style.getPropertyValue('--ink').trim() || '#1F2937';
  var muted = style.getPropertyValue('--muted').trim() || '#6B7280';
  var rule = style.getPropertyValue('--rule').trim() || '#E5DFD4';
  var bg2 = style.getPropertyValue('--bg2').trim() || '#F2EEE6';
  var success = style.getPropertyValue('--success').trim() || '#4CAF7D';
  var danger = style.getPropertyValue('--danger').trim() || '#D96C5C';

  // --- Radar: 知识点掌握度 ---
  var radar = echarts.init(document.getElementById('chart-radar'), null, { renderer: 'svg' });
  radar.setOption({
    tooltip: { appendToBody: true },
    animation: false,
    legend: {
      data: ['当前掌握度', '两周后预期'],
      bottom: 0,
      textStyle: { color: ink, fontSize: 12 }
    },
    radar: {
      indicator: [
        { name: '拼音', max: 100 },
        { name: '字词', max: 100 },
        { name: '阅读理解', max: 100 },
        { name: '100以内加减', max: 100 },
        { name: '表内乘法', max: 100 },
        { name: '简单应用题', max: 100 },
        { name: '时间单位', max: 100 }
      ],
      center: ['50%', '50%'],
      radius: '65%',
      name: { color: ink, fontSize: 13, fontWeight: 500 },
      splitArea: {
        areaStyle: {
          color: [bg2, 'rgba(255,255,255,0.5)', bg2, 'rgba(255,255,255,0.5)']
        }
      },
      splitLine: { lineStyle: { color: rule } },
      axisLine: { lineStyle: { color: rule } }
    },
    series: [{
      type: 'radar',
      data: [
        {
          value: [82, 75, 58, 78, 45, 52, 68],
          name: '当前掌握度',
          lineStyle: { color: accent, width: 2.5 },
          areaStyle: { color: accent + '55' },
          itemStyle: { color: accent }
        },
        {
          value: [95, 90, 75, 92, 78, 70, 88],
          name: '两周后预期',
          lineStyle: { color: accent2, width: 2.5, type: 'dashed' },
          areaStyle: { color: accent2 + '40' },
          itemStyle: { color: accent2 }
        }
      ]
    }]
  });
  window.addEventListener('resize', function() { radar.resize(); });

  // --- Revenue stacked bar ---
  var revenue = echarts.init(document.getElementById('chart-revenue'), null, { renderer: 'svg' });
  var months = ['M1', 'M2', 'M3', 'M4', 'M5', 'M6'];
  revenue.setOption({
    tooltip: { appendToBody: true, trigger: 'axis' },
    animation: false,
    legend: {
      data: ['免费注册', '订阅付费 ¥99/月', '视频号带货'],
      top: 0,
      textStyle: { color: ink, fontSize: 12 }
    },
    grid: { left: 50, right: 30, top: 50, bottom: 30 },
    xAxis: {
      type: 'category',
      data: months,
      axisLabel: { color: muted },
      axisLine: { lineStyle: { color: rule } }
    },
    yAxis: {
      type: 'value',
      name: '收入 (¥)',
      nameTextStyle: { color: muted },
      axisLabel: { color: muted },
      splitLine: { lineStyle: { color: rule, type: 'dashed' } }
    },
    series: [
      {
        name: '免费注册',
        type: 'bar',
        stack: 'total',
        data: [0, 0, 0, 0, 0, 0],
        itemStyle: { color: muted + '60' }
      },
      {
        name: '订阅付费 ¥99/月',
        type: 'bar',
        stack: 'total',
        data: [495, 1485, 3465, 6930, 13860, 24750],
        itemStyle: { color: accent }
      },
      {
        name: '视频号带货',
        type: 'bar',
        stack: 'total',
        data: [0, 120, 450, 1200, 2800, 5500],
        itemStyle: { color: accent2 }
      }
    ]
  });
  window.addEventListener('resize', function() { revenue.resize(); });

  // --- Cost comparison bar ---
  var cost = echarts.init(document.getElementById('chart-cost'), null, { renderer: 'svg' });
  cost.setOption({
    tooltip: { appendToBody: true, trigger: 'axis' },
    animation: false,
    grid: { left: 120, right: 30, top: 20, bottom: 40 },
    xAxis: {
      type: 'value',
      name: '月成本 (¥)',
      nameTextStyle: { color: muted },
      axisLabel: { color: muted },
      splitLine: { lineStyle: { color: rule, type: 'dashed' } }
    },
    yAxis: {
      type: 'category',
      data: ['视频号内容', 'APK 分发', '云服务器/CDN', 'LLM token 调用', '人员/设计'],
      axisLabel: { color: ink, fontSize: 13 },
      axisLine: { lineStyle: { color: rule } }
    },
    series: [
      {
        name: 'Loop Learning MVP',
        type: 'bar',
        data: [300, 0, 0, 150, 200],
        itemStyle: {
          color: accent,
          borderRadius: [0, 4, 4, 0]
        },
        barWidth: 18
      },
      {
        name: '传统 SaaS 教培 APP',
        type: 'bar',
        data: [2000, 500, 6000, 3000, 8000],
        itemStyle: {
          color: accent2,
          borderRadius: [0, 4, 4, 0]
        },
        barWidth: 18
      }
    ]
  });
  window.addEventListener('resize', function() { cost.resize(); });

})();
