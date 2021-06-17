# -*- coding: utf-8 -*-
import os

# get base
base = os.path.dirname(os.path.abspath(__file__))

# get grammar, resv, tot, non_term and term
grammar = []
resv = set()
tot = set()
non_term = set()

with open(os.path.join(base, 'grammar'), 'r') as file:
    while grammar := file.readline():
        words = grammar.strip().split(' ')
        grammar.append((words[0], tuple(words[1:])))
        for w in words:
            if w.isalpha() and w == w.upper():
                resv.add(w.lower())
        tot.update(words)
        non_term.add(words[0])

resv = resv.difference([
    'epsilon',
    'intc',
    'charc'
])
term = tot.difference(non_term)

# output resv
with open(os.path.join(base, 'resv'), 'w') as file:
    file.write('\n'.join(sorted(resv)) + '\n')

# ouput term
with open(os.path.join(base, 'term'), 'w') as file:
    file.write('\n'.join(sorted(term)) + '\n')

# calculate first
eps = 'EPSILON'
first: dict[tuple, set] = {}

for x in term:
    first[(x,)] = {x}

for x in non_term:
    first[(x,)] = set()
for x, infer in grammar:
    if x in non_term and infer[0] in term:
        first[(x,)].add(infer[0])

upd = True
while upd:
    upd = False
    for x, infer in grammar:
        if x in term:
            continue
        i = 0
        while i < len(infer) and eps in first[(infer[i],)]:
            i += 1
        l = len(first[(x,)])
        for j in range(i):
            first[(x,)].update(first[(infer[j],)].difference([eps]))
        first[(x,)].update(first[(infer[i],)] if i < len(infer) else [eps])
        upd |= len(first[(x,)]) > l

for _, infer in grammar:
    for s in range(len(infer)):
        l = []
        for e in range(s,len(infer)):
            l.append(infer[e])
            a = tuple(l)
            if a not in first:
                first[a] = set()
            i = 0
            while i < len(a) and eps in first[(a[i],)]:
                i += 1
            for j in range(i):
                first[a].update(first[(a[j],)].difference([eps]))
            first[a].update(first[(a[i],)] if i < len(a) else [eps])


# calculate follow
start = 'Program'
eof = '#'
follow: dict[str, set] = {}

for x in non_term:
    follow[x] = set()

follow[start] = {eof}

upd = True
while upd:
    upd = False
    for x, infer in grammar:
        if x in term:
            continue
        for i, y in enumerate(infer):
            if y in term:
                continue
            l = len(follow[y])
            b = []
            for j in range(i + 1, len(infer)):
                b.append(infer[j]) 
            b = tuple(b if b else [eps])
            if eps in first[b]:
                follow[y].update(first[b].difference([eps]).union(follow[x]))
            else:
                follow[y].update(first[b])
            upd |= len(follow[y]) > l


# calculate predict
predict = []
        
for x, infer in grammar:
    if eps in first[infer]:
        predict.append(first[infer].difference([eps]).union(follow[x]))
    else:
        predict.append(first[infer])


# output predict
with open(os.path.join(base, 'predict'), 'w') as file:
    for s in predict:
        file.write(' '.join(sorted(s)) + '\n')
