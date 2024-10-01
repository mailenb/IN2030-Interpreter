ukedag = ['man', 'tirs', 'ons', 'tors', 'fre', 'lør', 'søn']
m_leng = [0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
m_navn = [0] * (12 + 1)
m_navn[1] = 'januar'; m_navn[2] = 'februar'; m_navn[3] = 'mars'
m_navn[4] = 'april'; m_navn[5] = 'mai'; m_navn[6] = 'juni'
m_navn[7] = 'juli'; m_navn[8] = 'august'; m_navn[9] = 'september'
m_navn[10] = 'oktober'; m_navn[11] = 'november'; m_navn[12] = 'desember'
def finn_maaned(m_id):
  for m_ix in range(1, len(m_navn)):
    if m_navn[m_ix] == m_id: return m_ix
  return 0

def les_maaned():
  while True: 
    m_id = input('Oppgi en måned: ')
    m_num = finn_maaned(m_id)
    if m_num > 0: return m_num
    print('Ulovlig måned!')

def er_skuddaar(aa):
  return aa % 4 == 0 and aa % 100 or aa % 400 == 0

def finn_m_leng(m, aa):
  if m == 2 and er_skuddaar(aa): return 29
  return m_leng[m]

def finn_dag_nr(aa, m, d):
  d_nr = d
  for aax in range(1900, aa):
    d_nr = d_nr + 365
    if er_skuddaar(aax): d_nr = d_nr + 1
  for mx in range(1, m):
    d_nr = d_nr + finn_m_leng(mx, aa)
  return d_nr

aar = int(input('Oppgi et år: '))
maaned = les_maaned()
dag = int(input('Oppgi en dag: '))
u_dag = ukedag[(6 + finn_dag_nr(aar, maaned, dag)) % 7] + 'dag'
print(str(dag) + '.', m_navn[maaned], aar, 'er en', u_dag)
