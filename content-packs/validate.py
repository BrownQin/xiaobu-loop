#!/usr/bin/env python3
"""题库内容包校验 + 合并。用法：python3 validate.py [--merge]"""
import csv, sys, os, glob, collections

BASE = os.path.dirname(os.path.abspath(__file__))
HEADER = ["id","subject","knowledgePoint","difficulty","type","content","options","answer","explanation","tags","anchorCode","cognitiveLevel","variantType","enabled","stage"]

# 锚点 -> (subject, stage)
ANCHORS = {}
def add(codes, subj, stage):
    for c in codes: ANCHORS[c] = (subj, stage)

add("kg-count-10 kg-num-compare kg-num-compose kg-classify-compare kg-add-10 kg-sub-10 kg-add-sub-10 kg-shape-space kg-position kg-pattern kg-size kg-mass kg-time-day kg-money-awareness".split(), "math", "KG")
add("kg-pinyin-entry kg-pinyin-initial kg-pinyin-final kg-pinyin-tone kg-pinyin-blend kg-char-recognize kg-char-stroke kg-word-meaning kg-listen-express kg-speak-sentence kg-story-picture".split(), "chinese", "KG")
add("math-add-carry math-sub-borrow g2-addsub-mixed g2-addsub-word math-mul-meaning math-mul-table g2-mul-word g2-div-meaning g2-div-table g2-div-remainder g2-num-readwrite g2-num-compare g2-num-placevalue g2-length-unit g2-mass-unit g2-time-clock g2-money g2-shape-recognize g2-angle g2-symmetry-move g2-data-collect g2-pattern".split(), "math", "PRIMARY_LOW")
add("chinese-pinyin-polyphone g2-pinyin-similar g2-char-recognize g2-char-form g2-word-accumulate chinese-reading-main-idea g2-reading-detail g2-writing-sentence".split(), "chinese", "PRIMARY_LOW")

LEVELS = {"MEMORY","UNDERSTANDING","APPLICATION"}
VARIANTS = {"TEXT","DIAGRAM","CONTEXT","EXPRESSION"}
TYPES = {"choice","fill","word"}

def main():
    merge = "--merge" in sys.argv
    files = sorted(glob.glob(os.path.join(BASE, "pack*.csv")))
    all_rows = []
    errors = []
    seen = {}
    per_anchor = collections.Counter()

    for f in files:
        name = os.path.basename(f)
        with open(f, newline="", encoding="utf-8-sig") as fh:
            rows = list(csv.reader(fh))
        if not rows:
            errors.append(f"{name}: 空文件"); continue
        if [c.strip() for c in rows[0]] != HEADER:
            errors.append(f"{name}: 表头不符 -> {rows[0]}"); continue
        data = [r for r in rows[1:] if any(x.strip() for x in r)]
        for i, r in enumerate(data, start=2):
            tag = f"{name}:{i}"
            if len(r) != 15:
                errors.append(f"{tag}: 列数 {len(r)} != 15"); continue
            (_, subject, kp, diff, typ, content, options, answer, expl, tags, anchor, level, variant, enabled, stage) = [x.strip() for x in r]
            if anchor not in ANCHORS:
                errors.append(f"{tag}: 未知锚点 {anchor}"); continue
            exp_subj, exp_stage = ANCHORS[anchor]
            if subject != exp_subj: errors.append(f"{tag}: subject {subject} 应为 {exp_subj}")
            if stage != exp_stage: errors.append(f"{tag}: stage {stage} 应为 {exp_stage}")
            if not diff.isdigit() or not (1 <= int(diff) <= 5): errors.append(f"{tag}: difficulty 非法 {diff}")
            if typ not in TYPES: errors.append(f"{tag}: type 非法 {typ}")
            if level not in LEVELS: errors.append(f"{tag}: level 非法 {level}")
            if variant not in VARIANTS: errors.append(f"{tag}: variant 非法 {variant}")
            if typ == "choice":
                if not options: errors.append(f"{tag}: 选择题缺 options")
                elif answer.upper() not in [o.split(".")[0].strip().upper() for o in options.split("|")]:
                    errors.append(f"{tag}: 答案 {answer} 不在选项中")
            if not content: errors.append(f"{tag}: content 为空")
            if content in seen: errors.append(f"{tag}: 题干重复（与 {seen[content]}）")
            else: seen[content] = tag
            if "，" in answer or "," in answer:
                pass
            all_rows.append(r)
            per_anchor[anchor] += 1

    print(f"文件数 {len(files)}，题目 {len(all_rows)}")
    print("每锚点题数：")
    for a in sorted(per_anchor): print(f"  {a}: {per_anchor[a]}")
    missing = [a for a in ANCHORS if per_anchor[a] == 0]
    if missing: print("缺题锚点：", ", ".join(missing))
    low = [(a, per_anchor[a]) for a in ANCHORS if 0 < per_anchor[a] < 6]
    if low: print("题量偏少：", low)
    print(f"问题 {len(errors)} 条")
    for e in errors[:80]: print("  ", e)
    if errors:
        print("校验未通过，未合并"); return
    if merge:
        out = os.path.join(BASE, "questions_v2.csv")
        with open(out, "w", newline="", encoding="utf-8") as fh:
            w = csv.writer(fh)
            w.writerow(HEADER)
            w.writerows(all_rows)
        print("已合并 ->", out, len(all_rows), "题")

if __name__ == "__main__":
    main()
