from utils.fifoqueue import FIFOQueue

class TrainedModel():
    def __init__(self, path, optimizer, loss_fn, loader):
        self.__path = path
        self.__loader = loader
        self.__loss_fn = loss_fn
        self.__optimizer = optimizer
        # public attributes
        self.rsmes = FIFOQueue()
    
    def set_loader(self, loader):
        self.__loader = loader
    
    def get_loader(self):
        return self.__loader

    def get_loss_fn(self):
        return self.__loss_fn
    
    def get_optimizer(self):
        return self.__optimizer
    
    def get_path(self):
        return self.__path
