class Subject:
	# Represents what is being observed
	def __init__(self):
		self._observers = []

	def notify(self, name, modifier = None):
		for observer in self._observers:
			if modifier != observer:
				observer.retrain(name)

	def attach(self, observer):
		if observer not in self._observers:
			self._observers.append(observer)

	def detach(self, observer):
		try:
			self._observers.remove(observer)
		except ValueError:
			pass

class Data(Subject):
	def __init__(self, name =''):
		Subject.__init__(self)
		self.name = name
		self._data = 0

	@property
	def data(self):
		return self._data

	@data.setter
	def data(self, value):
		self._data = value
		if(value%100 == 0): 
		    self.notify(self.name)

