import sys
import StringIO

if __name__ == '__main__':
	file_name = sys.argv[1]
	f = open(file_name, 'r')
	ipt = StringIO.StringIO()
	'''
	with open(file_name) as f:
		for l in f.readlines():
			ipt.write(l)

	'''
	output = StringIO.StringIO()
	space = 0
	for c in f.read():
		if c == ' ':
			space += 1
			if space == 4:
				output.write('\t')
				space = 0
		else:
			i = 0
			while i < space:
				output.write(' ')
				i += 1
			space = 0
			output.write(c)

	with open(sys.argv[2], 'w+') as nf:
		nf.write(output.getvalue())






